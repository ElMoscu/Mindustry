package io.anuke.mindustry.ui.dialogs;

import io.anuke.arc.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.input.*;
import io.anuke.arc.scene.event.*;
import io.anuke.arc.scene.ui.layout.*;

import static io.anuke.mindustry.Vars.renderer;

public class MinimapDialog extends FloatingDialog{

    public MinimapDialog(){
        super("$minimap");
        setFillParent(true);

        shown(this::setup);

        addCloseButton();
        shouldPause = true;
        titleTable.remove();
        onResize(this::setup);
    }

    public void drawBackground(float x, float y){
        drawDefaultBackground(x, y);
    }

    void setup(){
        cont.clearChildren();

        cont.table("pane", t -> {
            t.addRect((x, y, width, height) -> {
                if(renderer.minimap.getRegion() == null) return;
                Draw.color(Color.WHITE);
                Draw.alpha(parentAlpha);
                Draw.rect(renderer.minimap.getRegion(), x + width / 2f, y + height / 2f, width, height);

                if(renderer.minimap.getTexture() != null){
                    renderer.minimap.drawEntities(x, y, width, height);
                }
            }).grow();
        }).size(Math.min(Core.graphics.getWidth() / 1.1f, Core.graphics.getHeight() / 1.3f) / UnitScl.dp.scl(1f)).padTop(-20f);

        cont.addListener(new InputListener(){
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountx, float amounty){
                renderer.minimap.zoomBy(amounty);
                return true;
            }
        });

        cont.addListener(new ElementGestureListener(){
            float lzoom = -1f;

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                lzoom = renderer.minimap.getZoom();
            }

            @Override
            public void zoom(InputEvent event, float initialDistance, float distance){
                if(lzoom < 0){
                    lzoom = renderer.minimap.getZoom();
                }
                renderer.minimap.setZoom(initialDistance / distance * lzoom);
            }
        });

        Core.app.post(() -> Core.scene.setScrollFocus(cont));
    }
}
