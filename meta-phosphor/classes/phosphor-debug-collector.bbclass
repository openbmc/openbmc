bmc_dump_path = "/var/lib/phosphor-debug-collector/dumps"
dreport_plugin_dir = "${datadir}/dreport.d/plugins.d"
dreport_include_dir = "${datadir}/dreport.d/include.d"
dreport_conf_dir = "${datadir}/dreport.d/conf.d"
dreport_dir = "${datadir}/dreport.d/"

# Make the links for a single user plugin script
# Create user directories based on the dump type value in the config section
# Create softlinks for the base scripts in the user directories
def install_dreport_user_script(dreport_conf, script_path, d):
    import re
    import configparser

    #Set variables
    config = ("config:")
    section = "DumpType"

    #Read the user types from the dreport_conf file
    configure = configparser.ConfigParser()
    conf_dir  = d.getVar('D', True) + d.getVar('dreport_conf_dir', True)
    confsource = os.path.join(conf_dir, dreport_conf)
    configure.read(confsource)

    #Extract the script name, and open the user script file
    dreport_dir = d.getVar('D', True) + d.getVar('dreport_dir', True)
    script = os.path.basename(script_path)
    file = open(script_path, "r")

    #softlink to the script
    srclink = os.path.join(d.getVar('dreport_plugin_dir', True), script)

    for line in file:
       if not config in line:
           continue

       revalue = re.search('[0-9]+.[0-9]+', line)
       if not revalue:
           bb.warn("Invalid format for config value =%s" % line)
           continue

       #Regex search to identify which directories get softlinks to the script
       parse_value = revalue.group(0)
       config_values = re.split(r'\W+', parse_value, 1)
       if(len(config_values) != 2):
           bb.warn("Invalid config value=%s" % parse_value)
           break;
       priority = config_values[1]
       types = [int(d) for d in str(config_values[0])]

       #For every dump type identified from 'types',create softlink to script
       for type in types:
           if not configure.has_option(section, str(type)):
               bb.warn("Invalid dump type id =%s" % (str(type)))
               continue

           #create directories based on the usertype
           typestr = configure.get(section, str(type))
           destdir = os.path.join(dreport_dir, ("pl_" + typestr + ".d"))
           if not os.path.exists(destdir):
               os.makedirs(destdir)

           #Create softlinks to the user script in the directories
           linkname = "E" + priority + script
           destlink = os.path.join(destdir, linkname)
           os.symlink(srclink, destlink)

    file.close()
