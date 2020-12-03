package tech.tinkmaster.fluent.service.operator;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.persistence.file.OperatorStorage;

@Service
public class OperatorService {

  @Autowired OperatorStorage storage;

  public List<String> list() {
    return storage.list();
  }

  public Operator get(String name) throws IOException {
    return storage.get(name);
  }

  public void updateOrCreate(Operator operator) throws IOException {
    storage.updateOrCreate(operator);
  }

  public void delete(String name) throws IOException {
    storage.delete(name);
  }
}
