<?php
  include( "conex.php" );
  $mysqli = conectar( );
  
  $sql = "SELECT f.*, u.nombre, u.email FROM pistones.fotografias f LEFT JOIN pistones.usuarios u ON f.id_usuario = u.id_usuario;";
  $res = $mysqli->query( $sql );
  $max = $res->num_rows;
  
  for( $i=0; $i<$max; $i++ )
  {
    $res->data_seek( $i );
    $obj = $res->fetch_object( );
    
    $id_fotografia[$i] = $obj->id_fotografia;
    $id_usuario[$i] = $obj->id_usuario;
    $descripcion[$i] = $obj->descripcion;
    $latitud[$i] = $obj->latitud;
    $longitud[$i] = $obj->longitud;
    $url_thumb[$i] = $obj->url_thumb;
    $url_fotografia[$i] = $obj->url_fotografia;
    $fecha_hora[$i] = $obj->fecha_hora;
    $nombre[$i] = $obj->nombre;
    $email[$i] = $obj->email;
  }
  
  $res->close( );
?>


<!DOCTYPE html>
<html>
<title>FotoSync</title>
<head>
  <style type="text/css">
    html, body { height: 100%; margin: 0; padding: 0; }
    #capa-mapa { height: 100%; }
  </style>
</head>
<body>

<div id="capa-mapa"></div>
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBSWTZxpZLuQd-B6SqE0Cu2pTgje8S7kzU&amp;sensor=false"></script>
<script type="text/javascript">

var misPuntos = [
<?php
  for( $i=0; $i<$max; $i++ )
  {
    printf( "['%s', '%s', '%s', '%s', '<p>Subida por: %s <br />%s</p><p><a href=\"%s\" target=\"_blank\"><img src=\"%s\" height=\"300\" /></a></p>'], ", 
    $fecha_hora[$i], $latitud[$i], $longitud[$i], $url_fotografia[$i], $nombre[$i], $email[$i], $url_fotografia[$i], $url_fotografia[$i] );
  }
?>
];

function inicializaGoogleMaps() {
	var x = 20.733200;
	var y = -103.455350;

	var mapOptions = {
		zoom: 13,
		center: new google.maps.LatLng(x, y),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}

	var map = new google.maps.Map(document.getElementById("capa-mapa"), mapOptions);
	setGoogleMarkers(map, misPuntos);
}

var markers = Array();
var infowindowActivo = false;
function setGoogleMarkers(map, locations) {

	for(var i=0; i<locations.length; i++) {
		var elPunto = locations[i];
		var myLatLng = new google.maps.LatLng(elPunto[1], elPunto[2]);
		
		var iconTemp = {
    			url: elPunto[3],
    			scaledSize: new google.maps.Size(50, 50),
    			origin: new google.maps.Point(0,0),
    			anchor: new google.maps.Point(0, 0)
		};
		
		markers[i]=new google.maps.Marker({
			position: myLatLng,
			map: map,
			icon: iconTemp,
			title: elPunto[0]
		});
		markers[i].infoWindow=new google.maps.InfoWindow({
			content: elPunto[4]
		});
		google.maps.event.addListener(markers[i], 'click', function(){      
			if(infowindowActivo)
				infowindowActivo.close();
			infowindowActivo = this.infoWindow;
			infowindowActivo.open(map, this);
		});
	}
}

inicializaGoogleMaps();
</script>
</body>
</html>