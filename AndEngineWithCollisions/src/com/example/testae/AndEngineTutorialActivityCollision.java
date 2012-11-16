package com.example.testae;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;

import fr.happster.formationAndEngine.helpers.AccelerometerHelper;
import fr.happster.formationAndEngine.objects.Platform;
import fr.happster.formationAndEngine.objects.Player;
 

public class AndEngineTutorialActivityCollision extends SimpleBaseGameActivity implements IAccelerationListener{

 
    public static final int CAMERA_WIDTH = 480;
    public static final int CAMERA_HEIGHT = 800;
	private static final float PIXEL_TO_METER_RATIO_DEFAULT = 32;

    private Camera mCamera;
    private Scene mMainScene;
    private PhysicsWorld mPhysicsWorld;

	private float mGravityX;
	private float mGravityY;
	private int mFaceCount = 0;
	private Player mFace;
	private Body mFaceBody;
 
    /*
     * Le BitMapTextureAtlas sert de "cadre" pour charger des textures. 
     * Il est de taille définie de telle sorte que la texture qui va lui 
     * être attachée est affichée qu'à l'intérieur du "cadre".
     */
    
    private BitmapTextureAtlas mPlayerBitmapTextureAtlas;
    private TiledTextureRegion mPlayerTiledTextureRegion;
    private AccelerometerHelper mAccelerometerHelper;
    
    private ArrayList<Platform> mPlatforms = new ArrayList();
	private BitmapTextureAtlas mPlatformBitmapTextureAtlas;
	private TiledTextureRegion mPlatformTextureRegion;
 

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        mAccelerometerHelper = new AccelerometerHelper(this);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
    }
 
    @Override
    protected void onCreateResources() {
    	
        // on définit la taille du TextureAtlas et on lui affecte une texture (TieldTextureRegion). Elle est ensuite chargée en mémoire pour être affichée
        this.mPlayerBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
        this.mPlayerTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPlayerBitmapTextureAtlas, this, "face_box.png", 0, 0, 1, 1);
        this.mPlayerBitmapTextureAtlas.load();
        
        /* Mise en place de la texture pour les plateformes */
        this.mPlatformBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32);
        this.mPlatformTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPlatformBitmapTextureAtlas, this, "platform.png", 0, 0, 1, 1);
        this.mPlayerBitmapTextureAtlas.load();
    }
    
    private void makeSceneAndWorld()
    {
    	// logs pour contrôler le framerate (FPS) du jeu
        this.mEngine.registerUpdateHandler(new FPSLogger()); 
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        this.mPhysicsWorld.setContactListener(createContactListener());
 
        this.mMainScene = new Scene();
        this.mMainScene.setBackground(new Background(1, 1, 1));
        this.mMainScene.registerUpdateHandler(this.mPhysicsWorld);
    }
 
    private void prepareFrames()
    {
    	//preparation des formes
    	final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		
		//preparation des boxes
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef).setUserData("ground");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef).setUserData("roof");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData("left");
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData("right");
		
		//ajout des elements dans la scene
		this.mMainScene.attachChild(ground);
		/*this.mMainScene.attachChild(roof);
		this.mMainScene.attachChild(left);
		this.mMainScene.attachChild(right);*/
    }
    
    
    @Override
    protected Scene onCreateScene() 
    {
    	makeSceneAndWorld();      
    	prepareFrames();
       
        // centre la texture (Player) dans l'écran
        final float centerX = (CAMERA_WIDTH - this.mPlayerTiledTextureRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mPlayerTiledTextureRegion.getHeight()) / 2;
        addPlayer(centerX,centerY);
        addPlatforms();
        return this.mMainScene;
    }

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}
	
	private void outOfScreenX()
	{
		if (this.mFace.getX() + this.mFace.getWidth() < 0) 
		{
			final Vector2 v2 = Vector2Pool.obtain(4, 9);
			this.mFaceBody.setTransform(v2, 0);
                                                                           
            Vector2Pool.recycle(v2);
			Log.i("SORTIEX", "Sortie à gauche : " + String.valueOf(this.mFace.getX()));
		}
		
		
		if (this.mFace.getX() > mCamera.getSurfaceWidth()) 
		{
           /* final Vector2 v2 = Vector2Pool.obtain(PIXEL_TO_METER_RATIO_DEFAULT / this.mFace.getY(), PIXEL_TO_METER_RATIO_DEFAULT); 
            mFaceBody.setTransform(v2, 0);
                                                                           
            Vector2Pool.recycle(v2);*/
			Log.i("SORTIEX", "Sortie à droite : " +String.valueOf(this.mFace.getX()));
		}
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		this.mGravityX = pAccelerationData.getX();
		this.mGravityY = pAccelerationData.getY();
		
		//Log.i("CONTACT", String.valueOf(pAccelerationData.getY()));

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, 9);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
		//outOfScreenX();
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}
	
	private void addPlatforms()
	{
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
        mPlatforms.add(new Platform(100, 500, this.mPlatformTextureRegion, this.getVertexBufferObjectManager()));
        mPlatforms.add(new Platform(200, 600, this.mPlatformTextureRegion, this.getVertexBufferObjectManager()));
        mPlatforms.add(new Platform(300, 700, this.mPlatformTextureRegion, this.getVertexBufferObjectManager()));
           
        this.mFace.setPlatforms(mPlatforms);
        //this.mface.move();
        
        for (Platform Platform : mPlatforms) {
        	PhysicsFactory.createBoxBody(this.mPhysicsWorld, Platform, BodyType.StaticBody, objectFixtureDef).setUserData("plateform");
            this.mMainScene.attachChild(Platform);
        }
	}
	
	private void addPlayer(final float pX, final float pY) {
		this.mFaceCount++;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		mFace = new Player(pX, pY, this.mPlayerTiledTextureRegion, this.getVertexBufferObjectManager());
		mFace.setUserData("player");
		mFaceBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, mFace, BodyType.DynamicBody, objectFixtureDef);
		mFaceBody.setUserData("player");


		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mFace, mFaceBody, true, true));
		mFace.setUserData(mFaceBody);
		this.mCamera.setChaseEntity(mFace);
		this.mMainScene.attachChild(mFace);
	}

	private ContactListener createContactListener()
    {
        ContactListener contactListener = new ContactListener()
        {
            @Override
            public void beginContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                
                boolean collisionPlayerPlateform = x2.getBody().getUserData().equals("player")&&x1.getBody().getUserData().equals("plateform");
                boolean collisionPlateformPlayer = x2.getBody().getUserData().equals("plateform")&&x1.getBody().getUserData().equals("player");
                boolean collisionGroundPlayer = x2.getBody().getUserData().equals("player")&&x1.getBody().getUserData().equals("ground");
                                
                if (collisionPlayerPlateform || collisionPlateformPlayer || collisionGroundPlayer)
                {
                        //Log.i("CONTACT", "BETWEEN PLAYER AND PLATFORM!");
                		//saut
                        mFaceBody.setLinearVelocity(new Vector2(mFaceBody.getLinearVelocity().x, -11.5f));
                }
            }
 
            @Override
            public void endContact(Contact contact)
            {
                   
            }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
        };
        return contactListener;
    }
	
 
}