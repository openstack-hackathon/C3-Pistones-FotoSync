from shade import *

simple_logging(debug=True)
conn = openstack_cloud(cloud='pistones')

print "Selected image:"
image_id = '0e524d47-6b9f-403d-8eeb-d96bb344651d'
image = conn.get_image(image_id)
print(image)

print "\nSelected flavor:"
flavor_id = '2'
flavor = conn.get_flavor(flavor_id)
print(flavor)

ex_userdata = '''#!/usr/bin/env bash
curl -L -s https://raw.githubusercontent.com/rojovivo27/openstackTest/master/init.sh | bash -s --
'''
external_network = '1f2a91f7-e3c1-48c9-b1fb-320dface898a'

print('Checking for existing security groups...')
sec_group_name = 'default'
if conn.search_security_groups(sec_group_name):
    print('Security group already exists. Skipping creation.')
else:
    print('Creating security group.')
    conn.create_security_group(sec_group_name, 'network access for a web application.')
    conn.create_security_group_rule(sec_group_name, 80, 80, 'TCP')

print "\nServer creation:"
instance_name = 'PISTONES'
testing_instance = conn.create_server(wait=True, auto_ip=True,
    name=instance_name,
    image=image_id,
    flavor=flavor_id,
    userdata=ex_userdata,
    network=external_network,
    key_name='pistones',
    security_groups=[sec_group_name])
