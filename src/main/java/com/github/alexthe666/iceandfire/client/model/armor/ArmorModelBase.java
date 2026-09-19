package com.github.alexthe666.iceandfire.client.model.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;


public class ArmorModelBase extends HumanoidModel<HumanoidRenderState> {

    public ArmorModelBase(ModelPart p_170677_) {
        super(p_170677_);
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
