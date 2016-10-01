inherit obmc-phosphor-utils
DISCOVERY_SVC_PACKAGE ?= "${PN}"
python() {

    pn = d.getVar('DISCOVERY_SVC_PACKAGE', True)
    current_task = d.getVar('BB_CURRENTTASK',True)
    syscnfdir = d.getVar('sysconfdir', True)
    dest_dir = d.getVar('D', True)
    is_avahi_enabled = bb.utils.contains('DISTRO_FEATURES', 'avahi',
                                         'true', 'false', d)
    if is_avahi_enabled == 'true':
        set_append(d, 'RRECOMMENDS_%s' % pn, 'avahi-daemon')
        set_append(d, 'IMAGE_INSTALL', 'avahi-daemon')
        set_append(d, 'FILES_%s' % pn, os.path.join(syscnfdir, 'avahi',
                                                    'services/*.service'))

        set_append(d, 'AVAHI_SERVICES_DIR', os.path.join(dest_dir+syscnfdir,
                                                         'avahi', 'services'))
}

python discovery_services_postinstall() {

    def register_service_avahi(d, service_name, service_type, service_port):
        service_dir = d.getVar('AVAHI_SERVICES_DIR', True)
        service_file = service_dir + service_name + ".service"
        if not os.path.exists(service_dir):
            os.makedirs(service_dir)
            with open(service_file, 'w') as fd:
                fd.write('<?xml version="1.0" ?>\n')
                fd.write('<!DOCTYPE service-group SYSTEM "avahi-service.dtd">\n')
                fd.write('<service-group>\n')
                fd.write('        <name>"%s"</name>\n' % service_name)
                fd.write('        <service>\n')
                fd.write('                <type>"%s"</type>\n' % service_type)
                fd.write('                <port>"%s"</port>\n' % service_port)
                fd.write('        </service>\n')
                fd.write('</service-group>\n')
                fd.close()

    def register_services(d,pkg):
        for service in listvar_to_list(d, 'REGISTERED_SERVICES_%s' % pkg):
            svc_info = service.split(":")
            try:
                svc_name, svc_type, svc_port = svc_info
            except:
                continue
            is_avahi_enabled = bb.utils.contains('DISTRO_FEATURES', 'avahi',
                                                 'true', 'false', d)
            if is_avahi_enabled == 'true':
                register_service_avahi(d, svc_name, svc_type, svc_port)

    for pkg in listvar_to_list(d, 'DISCOVERY_SVC_PACKAGE'):
        register_services(d, pkg)

}
do_install[postfuncs] += "discovery_services_postinstall"

