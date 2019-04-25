SUMMARY = "web server scanner"
DESCRIPTION = "Nikto is an Open Source web server scanner which performs comprehensive tests against web servers"
SECTION = "security"
HOMEPAGE = "https://cirt.net/Nikto2"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRCREV = "f1bbd1a8756c076c8fd4f4dd0bc34a8ef215ae79"
SRC_URI = "git://github.com/sullo/nikto.git \
           file://location.patch"

S = "${WORKDIR}/git/program"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${datadir}
    install -d ${D}${datadir}/man/man1
    install -d ${D}${datadir}/doc/nikto
    install -d ${D}${sysconfdir}/nikto
    install -d ${D}${sysconfdir}/nikto/databases
    install -d ${D}${sysconfdir}/nikto/plugins
    install -d ${D}${sysconfdir}/nikto/templates

    install -m 0644 databases/db_404_strings    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_content_search    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_dictionary    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_dir_traversal    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_domino    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_drupal    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_embedded    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_favicon    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_headers    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_httpoptions    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_multiple_index    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_outdated    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_parked_strings    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_realms    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_server_msgs    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_tests    ${D}${sysconfdir}/nikto/databases
    install -m 0644 databases/db_variables    ${D}${sysconfdir}/nikto/databases

    install -m 0644 plugins/LW2.pm    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_apache_expect_xss.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_apacheusers.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_auth.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_cgi.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_clientaccesspolicy.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_content_search.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_cookies.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_core.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_dictionary_attack.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_dir_traversal.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_dishwasher.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_docker_registry.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_domino.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_drupal.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_embedded.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_favicon.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_fileops.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_headers.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_httpoptions.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_ms10_070.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_msgs.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_multiple_index.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_negotiate.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_origin_reflection.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_outdated.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_parked.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_paths.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_put_del_test.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_csv.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_html.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_json.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_nbe.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_sqlg.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_text.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_report_xml.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_robots.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_siebel.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_sitefiles.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_ssl.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_strutshock.plugin    ${D}${sysconfdir}/nikto/plugins
    install -m 0644 plugins/nikto_tests.plugin    ${D}${sysconfdir}/nikto/plugins

    install -m 0644 templates/htm_close.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_end.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_host_head.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_host_im.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_host_item.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_start.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_stop.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_start.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/htm_summary.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/xml_end.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/xml_host_head.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/xml_host_im.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/xml_host_item.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/xml_start.tmpl    ${D}${sysconfdir}/nikto/templates
    install -m 0644 templates/xml_summary.tmpl    ${D}${sysconfdir}/nikto/templates

    install -m 0644 nikto.conf    ${D}${sysconfdir}

    install -m 0755 nikto.pl    ${D}${bindir}/nikto
    install -m 0644 replay.pl    ${D}${bindir}
    install -m 0644 docs/nikto.1    ${D}${datadir}/man/man1

    install -m 0644 docs/CHANGES.txt    ${D}${datadir}/doc/nikto
    install -m 0644 docs/LICENSE.txt    ${D}${datadir}/doc/nikto
    install -m 0644 docs/nikto.dtd    ${D}${datadir}/doc/nikto
    install -m 0644 docs/nikto_manual.html    ${D}${datadir}/doc/nikto
}

RDEPENDS_${PN} = "perl libnet-ssleay-perl libwhisker2-perl \
    perl-module-getopt-long perl-module-time-local \
    perl-module-io-socket perl-module-overloading \
    perl-module-base perl-module-b perl-module-bytes"

