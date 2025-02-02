package pl.zenit.tuqan.generators.spring.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.execution.parameters.SpringCustomParams;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.generators.spring.FileGenerator;

class ServerAccessInterceptor extends FileGenerator {

    public ServerAccessInterceptor(TuqanExecutionParameters params) {
        super(params);
    }

    @Override public void createAt(File outputDir) throws IOException {
        String appName = params.getCustomParams().get(SpringCustomParams.APPLICATION_NAME.name());
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".accesscontrol";

        IndentedStringList text = new IndentedStringList(params.getBasic().getIndentSize());
        text.add("package " + pkg + ";");
        text.add("");
        
        text.add("import jakarta.servlet.http.HttpServletRequest;");
        text.add("import jakarta.servlet.http.HttpServletResponse;");
        text.add("import org.springframework.beans.factory.annotation.Autowired;");
        text.add("import org.springframework.stereotype.Component;");
        text.add("import org.springframework.web.method.HandlerMethod;");
        text.add("import org.springframework.web.servlet.HandlerInterceptor;");
        text.add("import java.lang.reflect.Method;");
        text.add("import java.security.MessageDigest;");
        text.add("import java.security.NoSuchAlgorithmException;");
        text.add("import java.util.Optional;");
        text.add("");
        text.add("@Component");
        text.add("public class AccessInterceptor implements HandlerInterceptor {");
        text.pushIndent();
        text.add("");
        text.add("@Override");
        text.add("public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)");
        text.add("throws Exception {");
        text.pushIndent();
        text.add("String passHeaderValue = request.getHeader(\"Bearer: \");");
        text.add("");
        text.add("if (handler instanceof HandlerMethod) {");
        text.pushIndent();
        text.add("Method method = ((HandlerMethod) handler).getMethod();");
        text.add("Class<?> clazz = ((HandlerMethod) handler).getBeanType();");
        text.add("");
        text.add("String clientIp = request.getRemoteAddr();");
        text.add("boolean admin = method.isAnnotationPresent(AdminAccess.class) || clazz.isAnnotationPresent(AdminAccess.class);");
        text.add("boolean user = method.isAnnotationPresent(UserAccess.class) || clazz.isAnnotationPresent(UserAccess.class);");
        text.add("boolean publ = method.isAnnotationPresent(PublicAccess.class) || clazz.isAnnotationPresent(PublicAccess.class);");
        text.add("");
        text.add("if (publ) {");
        text.pushIndent();
        text.add("return true;");
        text.popIndent();
        text.add("}");
        text.add("");
        text.add("if (admin) {");
        text.pushIndent();
        text.add("return true;");
        text.popIndent();
        text.add("}");
        text.add("");
        text.add("if (user) {");
        text.pushIndent();
        text.add("return true;");
        text.popIndent();
        text.add("}");
        text.add("");
        text.add("return false; //proper annotation not present");
        text.popIndent();
        text.add("}");
        text.add("return true; // ?");
        text.popIndent();
        text.add("}");
        text.popIndent();
        text.add("}");

        Files.write(new File(outputDir, "AccessInterceptor.java").toPath(), text.getAsStringList());
    }
}

        