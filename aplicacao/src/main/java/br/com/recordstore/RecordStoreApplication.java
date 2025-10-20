package br.com.recordstore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages="br.com.recordstore")
public class RecordStoreApplication {
  public static void main(String[] args){ SpringApplication.run(RecordStoreApplication.class, args); }
}
