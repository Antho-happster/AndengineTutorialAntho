package fr.happster.formationAndEngine.objects;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.example.testae.AndEngineTutorialActivityCollision;

import fr.happster.formationAndEngine.helpers.AccelerometerHelper;
 
public class Player extends GameObject {
 
 
    private static final float DEFAULT_VELOCITY = 100;
    private boolean jumping = false;
    private ArrayList<Platform> mPlatforms;
	private float mGravityX;
	private float mGravityY;

	public Player(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        
    }
 
 
@Override
   public void move() {
    	
    	//bouge le Sprite en fonction du mouvement dŽtectŽ sur l'axe x (TILT)
        //this.mPhysicsHandler.setVelocityX(-AccelerometerHelper.TILT * 100);
        //ajoute la rotation du Sprite lors du mouvement
        setRotation(-AccelerometerHelper.TILT * 7);
        OutOfScreenX();


        if(mPlatforms.size() > 0)
        {
        //gestion des collisions avec les plateformes
	        for (Platform platform : mPlatforms) {
	            if (this.collidesWith(platform)) {
	                //Log.v("objects.Player", "just collided with platform");
	            }
	        }
        }
    }
    
    private void OutOfScreenX() {
    	// OutOfScreenX (right)
        /*if (mX > AndEngineTutorialActivityCollision.CAMERA_WIDTH) { 
            mX = 0;
         // OutOfScreenX (left)
        } else if (mX < 0) { 
            mX = AndEngineTutorialActivityCollision.CAMERA_WIDTH;
        }   */
    }
    
	public void Jump() {
		final Body faceBody = (Body)this.getUserData();

		final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -2, this.mGravityY * -2);
		faceBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}     
 
	

	public ArrayList getPlatforms() {
		return mPlatforms;
	}


	public void setPlatforms(ArrayList mPlatforms) {
		this.mPlatforms = mPlatforms;
	}

}