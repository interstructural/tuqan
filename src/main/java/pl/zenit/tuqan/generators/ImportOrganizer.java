package pl.zenit.tuqan.generators;

import java.util.Arrays;
import java.util.List;

public class ImportOrganizer {

    public List<String> getCommonImports() {
        return Arrays.asList(
              "java.util.List",
              "java.util.ArrayList",
              "java.util.Arrays",
              "java.util.Date",
              "java.util.Optional",
              "java.util.function.Function"
        );
    }

    public List<String> getRestImports() {
        return Arrays.asList(
              "net.rest.HttpsRequester",
              "net.rest.HttpInputDecoder",
              "net.rest.HttpOutputEncoder"
        );
    }

    public List<String> getJaxWsImports() {
        return Arrays.asList(
              "javax.ws.rs.*",
              "javax.ws.rs.core.*"
        );
    }

    public List<String> getSpringServerImports() {
        return Arrays.asList(
              "org.springframework.beans.factory.annotation.Autowired",
              "org.springframework.data.jpa.repository.JpaRepository",
              "org.springframework.stereotype.Repository",
              "org.springframework.data.domain.PageRequest",
              "jakarta.persistence.*",
              "org.springframework.data.domain.Pageable",
              "org.springframework.web.bind.annotation.*",
              "org.springframework.http.ResponseEntity",
              "org.springframework.stereotype.Service",
              "jakarta.transaction.Transactional"
        );
    }
    
    public List<String> getSpringClientImports() {
        return Arrays.asList(
            "org.springframework.web.client.RestTemplate",
            "org.springframework.http.ResponseEntity",
            "org.springframework.core.ParameterizedTypeReference",
            "org.springframework.http.HttpMethod",
            "org.springframework.http.HttpEntity"
        );
    }

    public List<String> getSpringClientCustomAccessImports(String rootPackage) {
          return Arrays.asList(
                rootPackage + ".accesscontrol.AccessInterceptor",
                rootPackage + ".accesscontrol.AdminAccess",
                rootPackage + ".accesscontrol.UserAccess",
                rootPackage + ".accesscontrol.PublicAccess"
          );
    }

} //end of class ImportOrganizer
