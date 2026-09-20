package com.github.alexthe666.iceandfire.client.model.armor;

import java.util.function.BiFunction;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;


public class ArmorModelBase extends HumanoidModel<HumanoidRenderState> {
    private EquipmentSlot visibleSlot = EquipmentSlot.CHEST;

    public ArmorModelBase(ModelPart p_170677_) {
        super(p_170677_);
    }

    public void setVisibleSlot(EquipmentSlot slot) {
        this.visibleSlot = slot;
        applySlotVisibility();
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        super.setupAnim(state);
        applySlotVisibility();
    }

    private void applySlotVisibility() {
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.rightArm.visible = false;
        this.leftArm.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;
        switch (this.visibleSlot) {
            case HEAD -> {
                this.head.visible = true;
                this.hat.visible = true;
            }
            case CHEST -> {
                this.body.visible = true;
                this.rightArm.visible = true;
                this.leftArm.visible = true;
            }
            case LEGS -> {
                this.body.visible = true;
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
            }
            case FEET -> {
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }

    protected static CubeDeformation armorDeformation(boolean inner) {
        return inner ? LayerDefinitions.INNER_ARMOR_DEFORMATION : LayerDefinitions.OUTER_ARMOR_DEFORMATION;
    }

    /**
     * Bake a private native tree. Shared static roots make chest/legs/head
     * overwrite each other's {@code visible} flags under deferred submit.
     */
    protected static ModelPart bakeArmor(boolean inner, BiFunction<CubeDeformation, Float, MeshDefinition> mesh) {
        return mesh.apply(armorDeformation(inner), 0.0F).getRoot().bake(64, 64);
    }

    //this.(?<name>.*).addChild\(this.(?<name2>.*)\);
    //partdefinition.getChild("${name}").addOrReplaceChild("${name2},

    //this.(?<name>.*) = new AdvancedModelBox\(.*, (?<texX>[0-9]*), (?<texY>[0-9]*)\);
    //.addOrReplaceChild("${name}", CubeListBuilder.create().texOffs(${texX}, ${texY})

    //this.(?<name>.*).setPos\((?<x>.*), (?<y>.*), (?<z>.*)\);
    //PartPose.offsetAndRotation(${x}, ${y}, ${z},

    //this.(?<name>.*).addBox\((?<x>.*), (?<y>.*), (?<z>.*), (?<u>.*), (?<v>.*), (?<w>.*), 0.0F\);
    //.addBox(${x}, ${y}, ${z}, ${u}, ${v}, ${w})

    //(?<main>.addOrReplaceChild\("(?<name>.*)", Cube.*)\n.*(?<part>PartPose.*)\n.*(?<box>addBox.*)\n.*this.setRotateAngle\(.*\k<name>.*, (?<aX>.*), (?<aY>.*), (?<aZ>.*)\);
    //${main}.${box}, ${part}${aX}, ${aY}, ${aZ}));

    //(?<main>.addOrReplaceChild\("(?<name>.*)", Cube.*)\n.*(?<part>PartPose.*)\n.*(?<box>addBox.*\));\n
    //${main}.${box}, ${part});
}
