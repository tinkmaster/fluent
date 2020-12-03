package tech.tinkmaster.fluent.service.data;

import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.persistence.mongo.data.DataStructureDefinitionRepository;

@Service
public class DataStructureDefinitionService {

  DataStructureDefinitionRepository repository;

  //    public void save(DataStructureDefinition definition) {
  //        repository.save(definition);
  //    }
}
