<?php
  include( "conex.php" );
  $mysqli = conectar( );
  
  // Get image string posted from Android App
  $base = $_REQUEST['image'];
  // Get file name posted from Android App
  $filename = $_REQUEST['filename'];
  // Decode Image
  $binary = base64_decode( $base );
  header( 'Content-Type: bitmap; charset=utf-8' );
  // Images will be saved under 'www/imgupload/uplodedimages' folder
  $file = fopen( 'uploads/'.$filename, 'wb' );
  // Create File
  fwrite( $file, $binary );
  fclose( $file );
  
  // Get latitude and logitude from Android App
  $email = $_REQUEST['email'];
  $latitude = $_REQUEST['latitude'];
  $longitude = $_REQUEST['longitude'];
  
  $sql = "SELECT id_fotografia FROM fotografias ORDER BY id_fotografia";
  $res = $mysqli->query( $sql );
  $max = $res->num_rows;
  
  if( $max==0 )
  {
    $id_fotografia = 1;
  }
  else
  {
    $res->data_seek( $max-1 );
    $obj = $res->fetch_object( );

    $id_fotografia = $obj->id_fotografia;
    $id_fotografia++;
  }
  
  $sql = "SELECT id_usuario FROM usuarios WHERE email='$email'";
  $res = $mysqli->query( $sql );
  $max = $res->num_rows;
  
  if( $max!=0 )
  {
    $res->data_seek( 0 );
    $obj = $res->fetch_object( );
    
    $id_usuario = $obj->id_usuario;
  }
  
  date_default_timezone_set( "America/Mexico_City" );
  $fechahora = date( "Y-m-d H:i:s" );
  
  $filenamenew = date( "Y_m_d__H_i_s__" ).$id_fotografia.".jpg";
  rename( "uploads/".$filename, "uploads/".$filenamenew );
  
/*  
  $imagen = "thumb_".$filename;
  $imagen1 = "uploads/".$filename;
  $imagen2 = getimagesize( $imagen1 );
  $ancho = 50;
  $alto = 50;
  //CREAMOS LA IMAGEN
  $nuevo = imagecreatetruecolor( $ancho, $alto );
  $origen = imagecreatefromjpeg( $imagen1 );
  $imagenes = imagecopyresized( $nuevo, $origen, 0, 0, 0, 0, $ancho , $alto, $imagen2[0], $imagen2[1] );
  imagecopyresampled($nuevo, $origen, 0, 0, $ancho, $alto, 0, 0, 0, 0 );
  $url_thumb = "uploads/{$imagen}";
  //CREA IMAGEN Y GUARDA.
  imagejpeg( $nuevo, $url_thumb, 95 );
  imagedestroy( $nuevo );
  imagedestroy( $origen );
*/  
  
  $sql = "INSERT INTO fotografias VALUES( '$id_fotografia', '$id_usuario', '$filenamenew', '$latitude', '$longitude', 'uploads/$filenamenew',
  'uploads/$filenamenew', '$fechahora' )";
  $res = $mysqli->query( $sql );
  
  $mysqli->close( );
  
  exec( "python uploadImage2Swift.py $filenamenew uploads/$filenamenew" );
?>