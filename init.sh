#!/bin/sh

#Install Apache
sudo apt-get update
sudo apt-get install apache2 apache2-doc apache2-mpm-prefork apache2-utils libexpat1 ssl-cert -y

#Install mysql
echo mysql-server-5.1 mysql-server/root_password password pistones | debconf-set-selections
echo mysql-server-5.1 mysql-server/root_password_again password pistones | debconf-set-selections
apt-get install -y mysql-server
dpkg --get-selections | grep mysql

#CreateDB
mysql -u root -ppistones -e "create database pistones"
mysql -u root -ppistones -e "USE pistones; CREATE TABLE usuarios ( id_usuario int(6) NOT NULL, nombre varchar(50) NOT NULL, email varchar(50) NOT NULL, PRIMARY KEY (id_usuario) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;"
mysql -u root -ppistones -e "USE pistones; CREATE TABLE fotografias ( id_fotografia int(6) NOT NULL, id_usuario int(6) NOT NULL, descripcion varchar(50) , latitud varchar(50) NOT NULL, longitud varchar(50) NOT NULL, url_thumb varchar(100) NOT NULL, url_fotografia varchar(100) NOT NULL, fecha_hora datetime NOT NULL, PRIMARY KEY (id_fotografia) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;"

#Install php
sudo apt-get install libapache2-mod-php5 php5 php5-mcrypt php5-mysql -y
sudo service apache2 restart

#Upgrade php
sudo add-apt-repository ppa:ondrej/php
sudo apt-get update
sudo apt-get install php7.0 -y
sudo apt-get install php7.0-mysql -y

#Install Composer
php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
php -r "if (hash_file('SHA384', 'composer-setup.php') === 'e115a8dc7871f15d853148a7fbac7da27d6c0030b848d9b3dc09e2a0388afed865e6a3d6b3c0fad45c48e2b5fc1196ae') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
php composer-setup.php
php -r "unlink('composer-setup.php');"
sudo mv composer.phar /usr/local/bin/composer

#Install Git 
sudo apt-get install git -y
composer require php-opencloud/openstack 

#Copying landing page
wget https://raw.githubusercontent.com/rojovivo27/openstackTest/master/my-landing-page.html
sudo cp my-landing-page.html /var/www/html/index.html
wget https://raw.githubusercontent.com/rojovivo27/openstackTest/master/info.php
sudo cp info.php /var/www/html/info.php
wget https://raw.githubusercontent.com/rojovivo27/openstackTest/master/prueba.php
sudo cp prueba.php /var/www/html/prueba.php

#Working with phpOpenCloud
wget https://raw.githubusercontent.com/rojovivo27/openstackTest/master/create.php
sudo cp create.php /var/www/html/create.php
wget https://raw.githubusercontent.com/rojovivo27/openstackTest/master/list.php
sudo cp list.php /var/www/html/list.php

