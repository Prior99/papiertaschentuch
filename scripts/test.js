var crate;
var text;

PPTT.on("ready", function() {
	print("Script started up!");
	PPTT.createPhysicalEntity({
		model : PPTT.getModel("models/cube_world.obj"), 
		texture : PPTT.getTexture("textures/hull.png"),
		collisionType : "concave"
	});
	crate = PPTT.createPhysicalEntity({
		model : PPTT.getModel("models/crate.obj"),
		texture : PPTT.getTexture("textures/crate.png"),
		mass : 1000
	});
	text = PPTT.createText({
		text : "Hello, World!",
		position : new Vec2i(0, 0)
	});
	PPTT.createLight({
		position: new Vec3f(0, -6, 0), 
		color: new Vec3f(.8, .9, 1.)
	});
});

PPTT.on("gametick", function() {
	if(text !== undefined) {
		text.move(new Vec2i(1, 0));
	}
});