package tech.tinkmaster.fluent.api.server.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.sqlite.SQLiteDataSource;

public class MybatisConfig {

  @Value("${data.dir}")
  private String dataDirectory;

  @Bean(name = "dataSource")
  public DataSource dataSource() {
    SQLiteDataSource dataSource = new SQLiteDataSource();
    dataSource.setUrl(String.format("jdbc:sqlite:%s/fluent.db", dataDirectory));
    try {
      if (!Files.exists(Paths.get(dataDirectory))) {
        try {
          Files.createDirectory(Paths.get(dataDirectory));
          Files.createFile(Paths.get(dataDirectory).resolve("fluent.db"));
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ClassPathResource classPathResource = new ClassPathResource("database/init.sql");
        for (String sql : readFile(classPathResource.getInputStream())) {
          statement.addBatch(sql);
        }
        statement.executeBatch();
        statement.close();
      } else {
        if (!Files.isDirectory(Paths.get(dataDirectory))) {
          throw new IllegalArgumentException("Database path exists and it's not a directory");
        }
      }
    } catch (SQLException | IOException e) {
      throw new IllegalStateException(e);
    }
    return dataSource;
  }

  private List<String> readFile(InputStream inputStream) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;

    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    return Arrays.asList(sb.toString().split(";"));
  }

  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource());
    return sqlSessionFactoryBean.getObject();
  }
}
