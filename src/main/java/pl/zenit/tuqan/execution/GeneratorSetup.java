package pl.zenit.tuqan.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pl.zenit.tuqan.generators.GeneratorGroup;
import pl.zenit.tuqan.generators.dto.DtoGeneratorGroup;
import pl.zenit.tuqan.generators.spring.SpringGeneratorGroup;
import pl.zenit.tuqan.generators.db.DbGeneratorGroup;

public class GeneratorSetup {

    private final List<GeneratorGroup> list = new ArrayList<>();

    public GeneratorSetup(boolean dto, boolean spring, boolean db) {
        if (dto) list.add(new DtoGeneratorGroup());
        if (spring) list.add(new SpringGeneratorGroup());
        if (db) list.add(new DbGeneratorGroup());
    }

    public List<GeneratorGroup> asList() {
        return Collections.unmodifiableList(list);
    }
    
    public Optional<GeneratorGroup> getGroup(String name) {
        return list.stream().filter(p-> p.getName().equalsIgnoreCase(name)).findAny();
    }

}
