#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import get_bb_vars, bitbake

class Systemd(OESelftestTestCase):
    """
    Tests related to the systemd bbclass.
    """

    def getVars(self, recipe):
        self.bb_vars = get_bb_vars(
            [
                'BPN',
                'D',
                'INIT_D_DIR',
                'prefix',
                'systemd_system_unitdir',
                'sysconfdir',
            ],
            recipe,
        )

    def fileExists(self, filename):
        self.assertExists(filename.format(**self.bb_vars))

    def fileNotExists(self, filename):
        self.assertNotExists(filename.format(**self.bb_vars))

    def test_systemd_in_distro(self):
        """
        Summary:    Verify that no sysvinit files are installed when the
                    systemd distro feature is enabled, but sysvinit is not.
        Expected:   Systemd service file exists, but /etc does not.
        Product:    OE-Core
        Author:     Peter Kjellerstedt <peter.kjellerstedt@axis.com>
        """

        self.write_config("""
DISTRO_FEATURES:append = " systemd usrmerge"
DISTRO_FEATURES:remove = "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
""")
        bitbake("systemd-only systemd-and-sysvinit -c install")

        self.getVars("systemd-only")
        self.fileExists("{D}{systemd_system_unitdir}/{BPN}.service")

        self.getVars("systemd-and-sysvinit")
        self.fileExists("{D}{systemd_system_unitdir}/{BPN}.service")
        self.fileNotExists("{D}{sysconfdir}")

    def test_systemd_and_sysvinit_in_distro(self):
        """
        Summary:    Verify that both systemd and sysvinit files are installed
                    when both the systemd and sysvinit distro features are
                    enabled.
        Expected:   Systemd service file and sysvinit initscript exist.
        Product:    OE-Core
        Author:     Peter Kjellerstedt <peter.kjellerstedt@axis.com>
        """

        self.write_config("""
DISTRO_FEATURES:append = " systemd sysvinit usrmerge"
VIRTUAL-RUNTIME_init_manager = "systemd"
""")
        bitbake("systemd-only systemd-and-sysvinit -c install")

        self.getVars("systemd-only")
        self.fileExists("{D}{systemd_system_unitdir}/{BPN}.service")

        self.getVars("systemd-and-sysvinit")
        self.fileExists("{D}{systemd_system_unitdir}/{BPN}.service")
        self.fileExists("{D}{INIT_D_DIR}/{BPN}")

    def test_sysvinit_in_distro(self):
        """
        Summary:    Verify that no systemd service files are installed when the
                    sysvinit distro feature is enabled, but systemd is not.
        Expected:   The systemd  service file does not exist, nor does /usr.
                    The sysvinit initscript exists.
        Product:    OE-Core
        Author:     Peter Kjellerstedt <peter.kjellerstedt@axis.com>
        """

        self.write_config("""
DISTRO_FEATURES:remove = "systemd"
DISTRO_FEATURES:append = " sysvinit usrmerge"
VIRTUAL-RUNTIME_init_manager = "sysvinit"
""")
        bitbake("systemd-only systemd-and-sysvinit -c install")

        self.getVars("systemd-only")
        self.fileNotExists("{D}{systemd_system_unitdir}/{BPN}.service")
        self.fileNotExists("{D}{prefix}")
        self.fileNotExists("{D}{sysconfdir}")
        self.fileExists("{D}")

        self.getVars("systemd-and-sysvinit")
        self.fileNotExists("{D}{systemd_system_unitdir}/{BPN}.service")
        self.fileNotExists("{D}{prefix}")
        self.fileExists("{D}{INIT_D_DIR}/{BPN}")
