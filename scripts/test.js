var crate;
var text;

PPTT.on("ready", function() {
	print("Script started up!");
	PPTT.createPhysicalEntity({
		model : PPTT.getModel("models/cube.obj"),
		texture : PPTT.getTexture("textures/grass_ground.png")
	})
	.setPosition(new Vec3f(0, -10, 0));
	text = PPTT.createText({
		text : "Hello, World!",
		position : new Vec2i(0, 0)
	});
	PPTT.createLight({
		position: new Vec3f(-6, -6, -6), 
		color: new Vec3f(.8, .9, 1.)
	});
	for(var x = -6; x < 6; x+= 1) {
		for(var z = -6; z < 6; z+= 1) {
			PPTT.createEntity({
				model : PPTT.getModel("models/grass.obj"),
				texture : PPTT.getTexture("textures/grass.png")
			})
			.setPosition(new Vec3f(x, -.6, z));
		}
	}
});