package pl.zenit.tuqan.generators.literals.php;

import java.util.List;

import pl.zenit.tuqan.generators.spring.SpringUsedScope;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;

public class PhpScope extends SpringUsedScope {

      public PhpScope(TuqanScopeclass scope) {
            super(scope);
      }

      public List<PhpField> fields() {
            return fields(PhpField::new);
      }


      /*
      public CRUD crud() {
            return new CRUD();
      }      
      public class CRUD {
            public String methodRemove() {
                  return "remove" + dtoName();
            }
            public String methodCreate() {
                  return "create" + dtoName();
            }
            public String methodUpdate() {
                  return "update" + dtoName();
            }
            public String methodGetAll() {
                  return "getAll" + dtoName();
            }
            public String methodGet() {
                  return "get" + dtoName();
            }
      }
      
      public REST rest() {
            return new REST();
      }
      public class REST {
            public String methodDelete() {
                  return "delete" + dtoName();
            }
            public String methodDeleteAll() {
                  return "deleteAll" + dtoName();
            }
            public String methodAdd() {
                  return "add" + dtoName();
            }
            public String methodAddAll() {
                return "addAll" + dtoName();
            }
            public String methodUpdate() {
                  return "update" + dtoName();
            }
            public String methodUpdateAll() {
                  return "updateAll" + dtoName();
            }            
            public String methodGet() {
                  return "get" + dtoName();
            }
            public String methodGetAll() {
                  return "getAll" + dtoName();
            }
            public String methodCount() {
                  return "count" + dtoName();
            }

            public String endpointDelete() {
                  return asTuqanObject().getName().toLowerCase() + "/delete";
            }
            public String endpointAdd() {
                  return asTuqanObject().getName().toLowerCase() + "/add";
            }
            public String endpointUpdate() {
                  return asTuqanObject().getName().toLowerCase() + "/update";
            }
            public String endpointGetAll() {
                  return asTuqanObject().getName().toLowerCase() + "/all";
            }
            public String endpointGet() {
                  return asTuqanObject().getName().toLowerCase() + "/get/{id}";
            }
            
            public List<RestField> fields() {
                  List<RestField> list = new ArrayList<>();
                  PhpScope.this.fields().stream().forEachOrdered(f-> list.add(new RestField(f)));
                  return list;
            }
            
            public RestField paramId() {
                  return new RestField(TuqanField.FIELD_ID);
            }
        
      }

      public SpringNames spring() {
            return new SpringNames();
      }
      public class SpringNames {
            public String controllerName() { return dtoName() + "Controller"; }
            public String repositoryName() { return dtoName() + "Repository"; }
            public String modelName() { return dtoName() + "Model"; }
            public String modelListName() { return "List<" + modelName() + ">"; }
            public String daoName() { return dtoName() + "Dao"; }
            public String crudGuiName() { return dtoName() + "CrudGui"; }
            
            public String baseRequestUrlName() {
                  return dtoName().toLowerCase();
            }
            
            public SpringService service() {
                return new SpringService();
            }
            public class SpringService {
                public String interfaceName() { return dtoName() + "Service"; }
                public String implementationName() { return dtoName() + "ServiceImpl"; }
                public String entityName() { 
                    return CodeFormatting.firstWordSmallLetter(dtoName()) + "Service";
                }
                
                public String methodBulkCreateName() { return "createAll"; }
                public String methodBulkUpdateName() { return "updateAll"; }
                public String methodBulkRemoveName() { return "deleteAll"; }
            }
            
            
      }
      */
} 
