package tech.tinkmaster.fluent.service.pipeline;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
import tech.tinkmaster.fluent.persistence.file.PipelineStorage;

@Service
public class PipelineService {

  @Autowired PipelineStorage storage;

  public List<String> list() {
    return storage.list();
  }

  public Pipeline get(String name) throws IOException {
    return storage.get(name);
  }

  public void updateOrCreate(Pipeline pipeline) throws IOException {
    storage.updateOrCreate(pipeline);
  }

  public void delete(String name) throws IOException {
    storage.delete(name);
  }
}
