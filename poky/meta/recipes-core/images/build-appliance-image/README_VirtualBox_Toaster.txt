
Running Toaster in VirtualBox
=============================

Toaster is launched via the command in VM:

    $ source toaster start webport=<IPADDR:PORT>

The interaction with Toaster web server is done via a host internet
browser.
The particular steps depend on the actual networking being used
by the VirtualBox.


Bridged Network
===============

Find out your VM network IP address:

    $ ifconfig

IP address is listed under eth0 inet addr.
It should be something like:
   inet addr:192.168.1.18

Launch the Toaster web server in VM:

    $ source toaster start webport=192.168.1.18:8000

Interact with the Toaster web server with your host browser using URL:

    http://192.168.1.18:8000


NAT Network
===========
Find out your VM network IP address:

    $ ifconfig

IP address is listed under eth0 inet addr.
For NAT network it should be something like:
   inet addr:10.0.2.15

When using NAT network, the VM web server can be accessed using
Port Forwarding.

Using the VirtualBox GUI, navigate to:
    Settings->Network->Adapter1

You should set:
    Attached to: NAT

Select "Advanced", click on "Port Forwarding"

This will open a new dialog box "Port Forwarding Rules".
Create a new rule that looks like this:

| Name  | Protocol | Host IP | Host Port | Guest IP | Guest Port |
+-------+----------+---------+-----------+----------+------------+
| Rule1 | TCP      |         | 8000      |          |  8000      |
------------------------------------------------------------------

Now we can launch the Toaster web server in VM:

    $ source toaster start webport=10.0.2.15:8000

Interact with the Toaster web server with your host browser using URL:

    http://127.0.0.1:8000








