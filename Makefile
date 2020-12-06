#
# Makefile for Fluent
#

DOCKER_REGISTRY_USERNAME = $(or $(shell printenv DOCKER_REGISTRY_USERNAME), "")
DOCKER_REGISTRY_PASSWORD = $(or $(shell printenv DOCKER_REGISTRY_PASSWORD), "")
GIT_REVISION = $(shell git describe --match= --always --abbrev=7 --dirty)
GOOGLE_JAVA_FORMAT = 1.6
MAVEN_TAG = $(or $(shell printenv MAVEN_TAG), "3.5.3-jdk-8")
REGISTRY = $(or $(shell printenv REGISTRY), "docker.io")
IMAGE_REPOSITORY = $(or $(shell printenv IMAGE_REPOSITORY), "tinkericlee/fluent")
MAVEN_CACHE_IMAGE_REPOSITORY = $(or $(shell printenv IMAGE_REPOSITORY), "tinkericlee/maven-cache-fluent")
DATE = $(or $(shell printenv DATE), $(shell git log -1 --format=%cd --date=format:%Y%m%d%H%M%S HEAD))
TIMESTAMP = $(or $(shell printenv TIMESTAMP), $(shell date -u +'%Y-%m-%dT%H:%M:%SZ'))
VERSION = $(or $(shell printenv VERSION), $(shell xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml))
BRANCH_NAME = $(or $(shell printenv BRANCH_NAME), $(shell git rev-parse --abbrev-ref HEAD))

.PHONY: all
all: format docker

.PHONY: test
test:
	mvn validate test

.PHONY: build-and-publish-docker
build-and-publish-docker: google-java-format docker docker-login docker-publish-master

.PHONY: build-and-publish-maven-cache-docker
build-and-publish-maven-cache-docker:
	@make _helper_build_maven_cache_dockerimage
	@make _helper_push_maven_cache_dockerimage

.PHONY: docker
docker:
	@make _helper_build_dockerimage

.PHONY: docker-login
docker-login:
	docker login --username=$(DOCKER_REGISTRY_USERNAME)  --password=$(DOCKER_REGISTRY_PASSWORD)

.PHONY: docker-publish-master
docker-publish-master:
	@make _helper_tag_dockerimage TARGET_IMAGE="$(REGISTRY)/$(IMAGE_REPOSITORY):$(DATE)-$(GIT_REVISION)"
	@make _helper_push_dockerimage TARGET_IMAGE="$(REGISTRY)/$(IMAGE_REPOSITORY):$(DATE)-$(GIT_REVISION)"
	@make _helper_tag_dockerimage TARGET_IMAGE="$(REGISTRY)/$(IMAGE_REPOSITORY):dev-latest"
	@make _helper_push_dockerimage TARGET_IMAGE="$(REGISTRY)/$(IMAGE_REPOSITORY):dev-latest"

.PHONY: docker-publish-release
docker-publish-release:
	@make _helper_tag_dockerimage TARGET_IMAGE="$(REGISTRY)/$(IMAGE_REPOSITORY):$(VERSION)"
	@make _helper_push_dockerimage TARGET_IMAGE="$(REGISTRY)/$(IMAGE_REPOSITORY):$(VERSION)"

.PHONY: format
format: google-java-format xml-format

.PHONY: google-java-format
google-java-format: tools/google-java-format.jar
	java -jar tools/google-java-format.jar \
	  --replace \
	  --set-exit-if-changed \
	  $(shell find . -name \*.java)

# use $$ to escape $ in Makefile
.PHONY: xml-format
xml-format:
	notPretty=0; \
	for f in *.xml; do \
	xmllint -o $$f.xmllint --format $$f; \
	origin_file=$$(echo -n $$(base64 -i $$f)); \
	linted_file=$$(echo -n $$(base64 -i $$f.xmllint)); \
	if [ "$$origin_file" = "$$linted_file" ]; then \
	  mv $$f.xmllint $$f && rm -f $$f.xmllint; \
	else \
	  mv $$f.xmllint $$f && rm -f $$f.xmllint && notPretty=1; \
	fi \
	done; \
	exit $$notPretty;

tools/google-java-format.jar:
	mkdir -p tools && \
	wget \
	  https://github.com/google/google-java-format/releases/download/google-java-format-$(GOOGLE_JAVA_FORMAT)/google-java-format-$(GOOGLE_JAVA_FORMAT)-all-deps.jar \
	  -O tools/google-java-format.jar

.PHONY: _helper_build_dockerimage
_helper_build_dockerimage:
	docker build \
	  --iidfile .imageid \
	  --tag $(IMAGE_REPOSITORY) \
	  --label tech.tinkmaster.fluent.version="$(VERSION)" \
	  --label tech.tinkmaster.fluent.created="$(TIMESTAMP)" \
	  --label tech.tinkmaster.fluent.revision="$(GIT_REVISION)" .
	docker tag $$(cat .imageid) $(IMAGE_REPOSITORY):$(VERSION)
	docker tag $$(cat .imageid) $(IMAGE_REPOSITORY):$(VERSION)-$(GIT_REVISION)

.PHONY: _helper_tag_dockerimage
_helper_tag_dockerimage:
	docker tag $(IMAGE_REPOSITORY):$(VERSION)-$(GIT_REVISION) $(TARGET_IMAGE)

.PHONY: _helper_build_maven_cache_dockerimage
_helper_build_maven_cache_dockerimage:
	docker build \
	  -f DockerfileForMavenCache \
	  --iidfile .imageid_maven_cache \
	  --tag $(IMAGE_REPOSITORY) \
	  --label tech.tinkmaster.fluent.version="$(VERSION)" \
	  --label tech.tinkmaster.fluent.created="$(TIMESTAMP)" \
	  --label tech.tinkmaster.fluent.revision="$(GIT_REVISION)" .
	docker tag $$(cat .imageid_maven_cache) $(MAVEN_CACHE_IMAGE_REPOSITORY)

.PHONY: _helper_push_maven_cache_dockerimage
_helper_push_maven_cache_dockerimage:
	docker push $(MAVEN_CACHE_IMAGE_REPOSITORY)