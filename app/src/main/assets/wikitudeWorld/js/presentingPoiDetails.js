var World = {

	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,

	markerList: [],

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

		//AR.context.destroyAll();
		
		// show radar
		PoiRadar.show();

		World.markerDrawable_idle = new AR.ImageResource("assets/marker_idle.png"),
		World.markerDrawable_selected = new AR.ImageResource("assets/marker_selected.png"),
		World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png"),

		document.getElementById("statusElement").innerHTML = 'Loading...';

		for (var i = 0; i < poiData.length; i++) {

			var singlePoi = {
				"id": poiData[i].id,
				"latitude": parseFloat(poiData[i].latitude),
				"longitude": parseFloat(poiData[i].longitude),
				"altitude": parseFloat(poiData[i].altitude),
				"title": poiData[i].name,
				"description": poiData[i].description,
                "icon": poiData[i].icon
			};

			World.markerList.push(new Marker(singlePoi));
		}

		document.getElementById("statusElement").innerHTML = 'Loaded';
		document.getElementById("statusElement").innerHTML = '';

	},

	//  user's latest known location, accessible via userLocation.latitude, userLocation.longitude, userLocation.altitude
	userLocation: null,

	// location updates
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {
		World.userLocation = {
			'latitude': lat,
			'longitude': lon,
			'altitude': alt,
			'accuracy': acc
		};
	},
	
	// Called to set distance radious from native code
	setRadiousMetters: function setRadiousMetters(radiousMetters) {
		// Radio de distancia del motor de RA
		AR.context.scene.cullingDistance = radiousMetters;
		// Radio de distancia en el que se dibujan elementos en el radar
		AR.radar.radius.maxDistance = radiousMetters;
	},

	onMarkerSelected: function onMarkerSelectedFn(marker) {
		// notify native environment
		document.location = "architectsdk://markerselected?id=" + marker.poiData.id;
	}

};

/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;