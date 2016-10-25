import os.path
from ConfigParser import SafeConfigParser


def get_intf_from_system():
#   /proc/net/dev
#   Inter- |   Receive          |  Transmit
#   iface   |bytes    packets    |bytes    packets
#    eth0: 489025780  646422     78927535  489701
#    lo: ..............
#    eth1: .................
    lines = open("/proc/net/dev", "r").readlines()
    intf_list = []
#   Not interested in first two lines
    for line in lines[2:]:
        if line.find(":") < 0:
            continue
        intf, data = line.split(":")
        intf = intf.strip()
        intf_list.append(intf)
    print intf_list
#    intf_list.remove('lo')
    return intf_list


def get_intf_from_conf(conf_file):
    if os.path.exists(conf_file):
        parser = SafeConfigParser()
        parser.optionxform = str
        parser.read(conf_file)
        sections = parser.sections()

        if "server" not in sections:
            raise NameError, "[server] section not found"

        interfaces = parser.get('server', 'interfaces')
        if interfaces == '':
            raise NameError, "Interfaces not configured"
        interfaces = interfaces.split()
        print interfaces
        return interfaces


if os.path.exists("/usr/sbin/avahi-autoipd") :
    conf_file = os.path.join('/etc', 'avahi', 'avahi-autoipd.conf')
    intf_list = []
#   If conf file is there and the interface is configured then get 
#   it from the conf else get the interfaces from the system.
    try:
        if os.path.exists(conf_file):
            print "getting the intf details from avahi-autoipd.conf"
            intf_list = get_intf_from_conf(conf_file)
        else:
           raise IOError, "Conf File doesn't exist"
        print intf_list
    except:
        print "Get the intf details from system"    
        intf_list = get_intf_from_system()

    import subprocess
    for intf in intf_list:
        subprocess.call(["/usr/sbin/avahi-autoipd", "-D", intf])
