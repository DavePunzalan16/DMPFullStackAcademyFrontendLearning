package com.dmpacademy.module;

import com.dmpacademy.module.dto.ModuleResponse;
import org.springframework.stereotype.Component;

@Component
public class ModuleMapper {

    public ModuleResponse toResponse(Module module) {
        return new ModuleResponse(
                module.getId(),
                module.getCourse().getId(),
                module.getTitle(),
                module.getOrderIndex(),
                module.getCreatedAt()
        );
    }
}
