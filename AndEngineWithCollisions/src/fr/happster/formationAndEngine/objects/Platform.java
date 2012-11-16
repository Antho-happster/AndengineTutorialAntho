package fr.happster.formationAndEngine.objects;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
 
public class Platform extends GameObject {
 

    public Platform(int pX, int pY, TiledTextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }
 
    @Override
    public void move() {
        // TODO Auto-generated method stub
    }

 
}