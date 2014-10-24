var text;
var sun;
var i = 0;
PT.on("ready", function() {
	print("Script started up!");
	PT.createPhysicalEntity({
		model : PT.getModel("models/cube_world.obj"),
		texture : PT.getTexture("textures/hull.png"),
		collisionType : "concave"
	})
	.setPosition(new Vec3f(0, -14, 0));
	text = PT.createText({
		text : "Hello, World!",
		position : new Vec2i(0, 0)
	});
	PT.createPointLight({
		position: new Vec3f(6, -22, -6), 
		color: new Vec3f(0.7, 0.7, 1),
		strength : 10
	});
	PT.createPointLight({
		position: new Vec3f(-6, -22, -6), 
		color: new Vec3f(1, 0.7, 0.7),
		strength : 10
	});
	PT.createPointLight({
		position: new Vec3f(6, -22, 6), 
		color: new Vec3f(0.7, 1, 0.7),
		strength : 10
	});
	PT.createPointLight({
		position: new Vec3f(-6, -22, 6), 
		color: new Vec3f(0.9, 0.9, 0.7),
		strength : 10
	});
	sun = PT.getSun();
	sun.setColor(new Vec3f(.3, .3, .3));
	i = 0;
});

PT.on("gametick", function() {
	if(i++ > 120) {
		i = 0;
		PT.createPhysicalEntity({
			model : PT.getModel("models/crate.obj"),
			texture : PT.getTexture("textures/crate.png"),
			mass : 2000
		})
		.setPosition(new Vec3f(0, -3, 0));
	}
});