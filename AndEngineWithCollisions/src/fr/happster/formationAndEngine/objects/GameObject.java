package fr.happster.formationAndEngine.objects;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class GameObject extends AnimatedSprite{
	
	public PhysicsHandler mPhysicsHandler;

	public GameObject(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) 
	{
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        //permet de faire bouger un Sprite selon un axe
        this.mPhysicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(this.mPhysicsHandler);
    }
	
	@Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        move();
        super.onManagedUpdate(pSecondsElapsed);
    }

	public abstract void move();
}
