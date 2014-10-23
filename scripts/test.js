var texture = PPTT.getTexture("textures/hull.png");
var model = PPTT.getModel("models/cube_world.obj");
PPTT.createPhysicalEntity({
	model : model, 
	texture : texture,
	collisionType : "concave"
});