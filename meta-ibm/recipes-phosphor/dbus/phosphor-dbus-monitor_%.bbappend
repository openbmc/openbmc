SYSTEMD_LINK_phosphor-msl-verify:append:ibm-ac-server = " ../phosphor-msl-verify.service:obmc-chassis-poweron@0.target.requires/phosphor-msl-verify.service"
SYSTEMD_LINK_phosphor-msl-verify:append:mihawk = " ../phosphor-msl-verify.service:obmc-chassis-poweron@0.target.requires/phosphor-msl-verify.service"
