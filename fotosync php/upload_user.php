<?php
  include( "conex.php" );
  $mysqli = conectar( );
  
  $nombre = $_REQUEST['nombre'];
  $email = $_REQUEST['email'];
  
  $sql = "SELECT id_usuario FROM usuarios ORDER BY id_usuario";
  $res = $mysqli->query( $sql );
  $max = $res->num_rows;
  
  if( $max==0 )
  {
    $id_usuario = 1;
  }
  else
  {
    $res->data_seek( $max-1 );
    $obj = $res->fetch_object( );

    $id_usuario = $obj->id_usuario;
    $id_usuario++;
  }
  
  $sql = "INSERT INTO usuarios values( '$id_usuario', '$nombre', '$email' )";
  $res = $mysqli->query($sql);
  
  $mysqli->close( );
?>