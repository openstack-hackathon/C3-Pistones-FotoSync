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
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://www.w3schools.com/lib/w3.css">
<body bgcolor="#000000">

<div class="w3-content" style="width:100%">

<?php
  for( $i=0; $i<$max; $i++ )
  {
?>
<div class="w3-display-container mySlides">
  <img src="<?php echo $url_fotografia[$i]; ?>" style="width:100%">
  <div class="w3-display-topleft w3-large w3-container w3-padding-16 w3-black">Subida por: <?php echo $nombre[$i]; ?></div>
</div>
<?php
  }
?>

<a class="w3-btn-floating w3-hover-dark-grey" style="position:absolute;top:45%;left:0" onclick="plusDivs(-1)"><</a>
<a class="w3-btn-floating w3-hover-dark-grey" style="position:absolute;top:45%;right:0" onclick="plusDivs(1)">></a>

</div>

<script>
var slideIndex = 1;
showDivs(slideIndex);

function plusDivs(n) {
  showDivs(slideIndex += n);
}

function showDivs(n) {
  var i;
  var x = document.getElementsByClassName("mySlides");
  if (n > x.length) {slideIndex = 1}
  if (n < 1) {slideIndex = x.length}
  for (i = 0; i < x.length; i++) {
     x[i].style.display = "none";
  }
  x[slideIndex-1].style.display = "block";
}
</script>

</body>
</html>