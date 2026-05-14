.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Checking for Vulnerabilities
****************************

Vulnerabilities in OpenEmbedded-Core (OE-Core)
==============================================

The Yocto Project has an infrastructure to track and address unfixed
known security vulnerabilities, as tracked by the public
:wikipedia:`Common Vulnerabilities and Exposures (CVE) <Common_Vulnerabilities_and_Exposures>`
database.

The Yocto Project maintains a `list of known vulnerabilities
<https://valkyrie.yocto.io/pub/non-release/patchmetrics/>`__
for packages in :term:`OpenEmbedded-Core (OE-Core)`, tracking the evolution of the number of
unpatched CVEs and the status of patches. Such information is available for
the current development version and for each supported release.

Security is a process, not a product, and thus at any time, a number of security
issues may be impacting :term:`OpenEmbedded-Core (OE-Core)`. It is up to the maintainers, users,
contributors and anyone interested in the issues to investigate and possibly fix them by
updating software components to newer versions or by applying patches to address them.
It is recommended to work with :term:`OpenEmbedded-Core (OE-Core)` upstream maintainers and submit
patches to fix them, see ":doc:`/contributor-guide/submit-changes`" for details.

Vulnerability check at build time
=================================

To enable a check for CVE security vulnerabilities in the specific image or
target you are building, run the following command from your :term:`Build
Directory`:

.. code-block:: console

   $ bitbake-config-build enable-fragment core/yocto/sbom-cve-check

Or add the following statement to a :term:`configuration file`::

   OE_FRAGMENTS += "core/yocto/sbom-cve-check"

This will enable the :ref:`ref-classes-sbom-cve-check` class and set the
recommended settings to use it.

The CVE database contains some old incomplete entries which have been deemed not
to impact :term:`OpenEmbedded-Core (OE-Core)`. These CVE entries can be excluded
from the check by adding the following statement::

   include conf/distro/include/cve-extra-exclusions.inc

With the :ref:`ref-fragments-core-yocto-sbom-cve-check` fragment enabled, the
:term:`BitBake` build of an image will try to map each compiled software
component recipe name and version information to the CVE database and generate
reports in the deployment directory (:term:`DEPLOY_DIR_IMAGE`), one of which
being: ``tmp/deploy/images/<machine>/<image-name>-<machine>.rootfs.sbom-cve-check.yocto.json``,
a report containing:

   -  Metadata about the software component like names and versions
   -  Metadata about the CVE issue such as description and NVD link
   -  For each software component, a list of CVEs which are possibly impacting this version
   -  Status of each CVE: ``Patched``, ``Unpatched`` or ``Ignored``

.. note::

   Another report named ``<image-name>-<machine>.rootfs.sbom-cve-check.spdx.json``
   is also generated: this is the enriched :term:`SPDX` file of the image
   containing the same information contained in the previous point, and a lot
   more metadata information on the packages included in the image. For more
   information on :term:`SPDX`, see the :doc:`/dev-manual/sbom` section of the
   Yocto Project Development Tasks Manual.

Each item in the ``"package"`` list corresponds to a package installed on the
built image. Each of these packages contain a number of CVE entries under the
``"issue"`` sub-list. These CVE can have the following statuses:

-  ``Patched`` means that a patch file to address the security issue
   has been applied.

-  ``Unpatched`` means that no patches to address the issue have been
   applied and that the issue needs to be investigated.

-  ``Ignored`` means that after analysis, it has been deemed to ignore the issue
   as it for example affects the software component on a different operating
   system platform.

For example, the report for the ``glibc`` package looks like this (simplified):

.. code-block:: json

   {
     "version": "1",
     "package": [
       {
         "name": "glibc",
         "layer": "core",
         "version": "2.43+git",
         "products": [
           {
             "product": "glibc",
             "cvesInRecord": "Yes"
           }
         ],
         "issue": [
           {
             "id": "CVE-2010-4756",
             "status": "Unpatched",
             "link": "https://nvd.nist.gov/vuln/detail/CVE-2010-4756",
             "summary": "The glob implementation in the GNU C Library (aka glibc or libc6) allows remote authenticated users to cause a denial of service (CPU and memory consumption) via crafted glob expressions that do not match any pathnames, as demonstrated by glob expressions in STAT commands to an FTP daemon, a different vulnerability than CVE-2010-2632.",
             "scorev2": "4.0",
             "scorev3": "0.0",
             "scorev4": "0.0",
             "modified": "2025-11-03T22:15:41.000",
             "vector": "NETWORK",
             "vectorString": "AV:N/AC:L/Au:S/C:N/I:N/A:P",
             "detail": "no-version-ranges",
             "description": "Check package version"
           },
           {
             "id": "CVE-2018-6551",
             "status": "Patched",
             "link": "https://nvd.nist.gov/vuln/detail/CVE-2018-6551",
             "summary": "The malloc implementation in the GNU C Library (aka glibc or libc6), from version 2.24 to 2.26 on powerpc, and only in version 2.26 on i386, did not properly handle malloc calls with arguments close to SIZE_MAX and could return a pointer to a heap region that is smaller than requested, eventually leading to heap corruption.",
             "scorev2": "7.5",
             "scorev3": "9.8",
             "scorev4": "0.0",
             "modified": "2024-11-21T04:10:53.000",
             "vector": "NETWORK",
             "vectorString": "CVSS:3.0/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
             "detail": "version-not-in-range"
           },
           {
             "id": "CVE-2019-1010022",
             "status": "Ignored",
             "link": "https://nvd.nist.gov/vuln/detail/CVE-2019-1010022",
             "summary": "GNU Libc current is affected by: Mitigation bypass. The impact is: Attacker may bypass stack guard protection. The component is: nptl. The attack vector is: Exploit stack buffer overflow vulnerability and use this bypass vulnerability to bypass stack guard. NOTE: Upstream comments indicate \"this is being treated as a non-security bug and no real threat.",
             "scorev2": "7.5",
             "scorev3": "9.8",
             "scorev4": "0.0",
             "modified": "2024-11-21T04:17:55.000",
             "vector": "NETWORK",
             "vectorString": "CVSS:3.0/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
             "description": "Upstream glibc maintainers dispute there is any issue and have no plans to address it further. this is being treated as a non-security bug and no real threat."
           }
         ],
         "cpes": ["cpe:2.3:*:*:glibc:2.43:*:*:*:*:*:*:*"]
       }
     ]
   }

At build time the :ref:`ref-classes-sbom-cve-check` class will also throw warnings about
``Unpatched`` CVEs (when :term:`SBOM_CVE_CHECK_SHOW_WARNINGS` is set to "1"):

.. code-block:: text

   WARNING: core-image-minimal-1.0-r0 do_sbom_cve_check: glibc-2.43+git: Found unpatched CVEs: CVE-2010-4756

Fixing CVE product name and version mappings
============================================

By default, :ref:`ref-classes-sbom-cve-check` uses the recipe name :term:`BPN` as CVE
product name when querying the CVE database. If this mapping contains false positives, e.g.
some reported CVEs are not for the software component in question, or false negatives like
some CVEs are not found to impact the recipe when they should, then the problems can be
in the recipe name to CVE product mapping. These mapping issues can be fixed by setting
the :term:`CVE_PRODUCT` variable inside the recipe. This defines the name of the software component in the
upstream `NIST CVE database <https://nvd.nist.gov/>`__.

The variable supports using vendor and product names like this::

   CVE_PRODUCT = "flex_project:flex westes:flex"

In this example we have two possible vendors names,  ``flex_project`` and ``westes``,
with the product name ``flex``. With this setting the ``flex`` recipe only maps to this specific
product and not products from other vendors with same name ``flex``.

Similarly, when the recipe version :term:`PV` is not compatible with software versions used by
the upstream software component releases and the CVE database, these can be fixed using
the :term:`CVE_VERSION` variable.

Note that if the CVE entries in the NVD database contain bugs or have missing or incomplete
information, it is recommended to fix the information there directly instead of working
around the issues possibly for a long time in :term:`OpenEmbedded-Core (OE-Core)` side recipes. Feedback to
NVD about CVE entries can be provided through the `NVD contact form <https://nvd.nist.gov/info/contact-form>`__.

Fixing vulnerabilities in recipes
=================================

Suppose a CVE security issue impacts a software component. In that case, it can
be fixed by updating to a newer version, by applying a patch, or by marking it
as patched via :term:`CVE_STATUS` variable flag. For :term:`OpenEmbedded-Core (OE-Core)` master
branches, updating to a more recent software component release with fixes is
the best option, but patches can be applied if releases are not yet available.

For stable branches, we want to avoid API (Application Programming Interface)
or ABI (Application Binary Interface) breakages. When submitting an update,
a minor version update of a component is preferred if the version is
backward-compatible. Many software components have backward-compatible stable
versions, with a notable example of the Linux kernel. However, if the new
version does or likely might introduce incompatibilities, extracting and
backporting patches is preferred.

Here is an example of fixing CVE security issues with patch files,
an example from the :oe_layerindex:`ffmpeg recipe for dunfell </layerindex/recipe/122174>`::

   SRC_URI = "https://www.ffmpeg.org/releases/${BP}.tar.xz \
              file://mips64_cpu_detection.patch \
              file://CVE-2020-12284.patch \
              file://0001-libavutil-include-assembly-with-full-path-from-sourc.patch \
              file://CVE-2021-3566.patch \
              file://CVE-2021-38291.patch \
              file://CVE-2022-1475.patch \
              file://CVE-2022-3109.patch \
              file://CVE-2022-3341.patch \
              file://CVE-2022-48434.patch \
          "

The recipe has both generic and security-related fixes. The CVE patch files are named
according to the CVE they fix.

When preparing the patch file, take the original patch from the upstream repository.
Do not use patches from different distributions, except if it is the only available source.

Modify the patch adding OE-related metadata. We will follow the example of the
``CVE-2022-3341.patch``.

The original `commit message <https://github.com/FFmpeg/FFmpeg/commit/9cf652cef49d74afe3d454f27d49eb1a1394951e.patch/>`__
is::

   From 9cf652cef49d74afe3d454f27d49eb1a1394951e Mon Sep 17 00:00:00 2001
   From: Jiasheng Jiang <jiasheng@iscas.ac.cn>
   Date: Wed, 23 Feb 2022 10:31:59 +0800
   Subject: [PATCH] avformat/nutdec: Add check for avformat_new_stream

   Check for failure of avformat_new_stream() and propagate
   the error code.

   Signed-off-by: Michael Niedermayer <michael@niedermayer.cc>
   ---
    libavformat/nutdec.c | 16 ++++++++++++----
    1 file changed, 12 insertions(+), 4 deletions(-)


For the correct operations of :ref:`ref-classes-sbom-cve-check`, it requires the CVE
identification in a ``CVE:`` tag of the patch file commit message using
the format::

   CVE: CVE-2022-3341

It is also required to add the ``Upstream-Status:`` tag with a link
to the original patch and sign-off by people working on the backport.
If there are any modifications to the original patch, note them in
the ``Comments:`` tag.

With the additional information, the header of the patch file in OE-core becomes::

   From 9cf652cef49d74afe3d454f27d49eb1a1394951e Mon Sep 17 00:00:00 2001
   From: Jiasheng Jiang <jiasheng@iscas.ac.cn>
   Date: Wed, 23 Feb 2022 10:31:59 +0800
   Subject: [PATCH] avformat/nutdec: Add check for avformat_new_stream

   Check for failure of avformat_new_stream() and propagate
   the error code.

   Signed-off-by: Michael Niedermayer <michael@niedermayer.cc>

   CVE: CVE-2022-3341

   Upstream-Status: Backport [https://github.com/FFmpeg/FFmpeg/commit/9cf652cef49d74afe3d454f27d49eb1a1394951e]

   Comments: Refreshed Hunk
   Signed-off-by: Narpat Mali <narpat.mali@windriver.com>
   Signed-off-by: Bhabu Bindu <bhabu.bindu@kpit.com>
   ---
    libavformat/nutdec.c | 16 ++++++++++++----
    1 file changed, 12 insertions(+), 4 deletions(-)

A good practice is to include the CVE identifier in the patch file name, the patch file
commit message and optionally in the recipe commit message.

:ref:`ref-classes-sbom-cve-check` will then capture this information and change the CVE
status to ``Patched`` in the generated reports.

If analysis shows that the CVE issue does not impact the recipe due to configuration, platform,
version or other reasons, the CVE can be marked as ``Ignored`` by using
the :term:`CVE_STATUS` variable flag with appropriate reason which is mapped to ``Ignored``.
The entry should have the format like::

   CVE_STATUS[CVE-2016-10642] = "cpe-incorrect: This is specific to the npm package that installs cmake, so isn't relevant to OpenEmbedded"

As mentioned previously, if data in the CVE database is wrong, it is recommended
to fix those issues in the CVE database (NVD in the case of
:term:`OpenEmbedded-Core (OE-Core)`) directly.

Note that if there are many CVEs with the same status and reason, those can be
shared by using the :term:`CVE_STATUS_GROUPS` variable.

When analyzing CVEs, it is recommended to:

-  study the latest information in `CVE database <https://nvd.nist.gov/vuln/search>`__.

-  check how upstream developers of the software component addressed the issue, e.g.
   what patch was applied, which upstream release contains the fix.

-  check what other Linux distributions like `Debian <https://security-tracker.debian.org/tracker/>`__
   did to analyze and address the issue.

-  follow security notices from other Linux distributions.

-  follow public `open source security mailing lists <https://oss-security.openwall.org/wiki/mailing-lists>`__ for
   discussions and advance notifications of CVE bugs and software releases with fixes.

Implementation details
======================

As :ref:`ref-classes-sbom-cve-check` is an external tool, its implementation is detailed on
the official documentation: https://sbom-cve-check.readthedocs.io/en/latest/index.html

Linux kernel vulnerabilities
============================

Since the Linux kernel became a CVE Numbering Authority (CNA), the number of
associated CVEs has increased dramatically. Security teams must address these
CVEs to meet regulatory and customer requirements. Automation on identifying
issues helps to reduce their workload.

:term:`OpenEmbedded-Core (OE-Core)` has two scripts that help to characterize
and filter CVEs affecting the Linux kernel:

-  ``openembedded-core/meta/recipes-kernel/linux/generate-cve-exclusions.py``
-  ``openembedded-core/scripts/contrib/improve_kernel_cve_report.py``

``generate-cve-exclusions.py``
------------------------------

When updating a kernel recipe, a helper script needs to be run manually to
update the :term:`CVE_STATUS` for the kernel recipe. The script can be used
for custom kernels.

First we need to get an updated version of the CVE information from the
`CVE Project`. Run it as follows:

.. code-block:: shell

   $ git clone https://github.com/CVEProject/cvelistV5 ~/cvelistV5

Or if you have already cloned it, you need to pull the latest data:

.. code-block:: shell

   $ git -C ~/cvelistV5 pull

Then, generate the :term:`CVE_STATUS` information for the desired version
of the kernel:

.. code-block:: shell

   $ ./generate-cve-exclusions.py ~/cvelistV5 <version> > cve-exclusion_<kernel_version>.inc

Example:

.. code-block:: shell

   $ git clone https://github.com/CVEProject/cvelistV5 ~/cvelistV5
   $ cd openembedded-core/meta/recipes-kernel/linux/
   $ ./generate-cve-exclusions.py ~/cvelistV5 6.12.27 > ~/meta-custom/recipes-kernel/linux/cve-exclusion_6.12.inc

Don't forget to update your kernel recipe with::

   include cve-exclusion_6.12.inc

Then the CVE information will automatically be added in the
``cve-check`` or :ref:`ref-classes-vex` report.

``improve_kernel_cve_report.py``
--------------------------------

The ``openembedded-core/scripts/contrib/improve_kernel_cve_report.py`` script
leverages CVE kernel metadata and the :term:`SPDX_INCLUDE_COMPILED_SOURCES`
variable to update an output ``.sbom-cve-check.yocto.json`` report file (see
section :ref:`security-manual/vulnerabilities:Vulnerability check at build time`
for details on these report files). It reduces CVE false positives by 70%-80%
and provide detailed responses for all kernel-related CVEs by analyzing the
files used to build the kernel. The script is decoupled from the build and
can be run outside of the :term:`BitBake` environment.

The script uses the output from the :ref:`ref-classes-vex` as input, together
with CVE information from the Linux kernel CNA to enrich the
report file with updated CVE information.

The file name can be specified as argument. Optionally, it can also use the
list of compiled files from the kernel :term:`SPDX` to ignore CVEs that are
not affected because the files are not compiled.

For this, BitBake uses the debug information to extract the sources used to
build a binary. Therefore, it needs to be configured in the kernel to extract
the kernel compiled files.

If you are using the ``linux-yocto`` recipe, enable it by adding the following
in a :term:`configuration file` or in a ``.bbappend``::

   KERNEL_EXTRA_FEATURES:append = " features/debug/debug-kernel.scc"

Or by editing your kernel configuration to include `DWARF4` debug information.

See the :ref:`kernel-dev/common:Changing the Configuration` section of the Yocto
Project Linux Kernel Development Manual for more information.

For the following example, we will consider that the kernel recipe used is
``linux-yocto``. Instructions also apply to other kernel recipes named
differently.

The sources for the kernel are stored under
``tmp/pkgdata/<MACHINE>/debugsources/linux-yocto-debugsources.json.zstd``. In
order to include the information into the :term:`SPDX` file to filter out
source files that are not used to compile the kernel, add the following in a
:term:`configuration file`::

   SPDX_INCLUDE_COMPILED_SOURCES:pn-linux-yocto = "1"

Finally, store either the ``recipe-linux-yocto.spdx.json`` or the
``linux-yocto-debugsources.json.zstd`` outside the :term:`build directory`.

The :term:`SPDX` file is under
``tmp/deploy/spdx/<spdx_version>/<MACHINE>/recipes/recipe-linux-yocto.spdx.json``

Once you have the input data, first you need to clone or fetch the latest CVE
information from https://git.kernel.org:

.. code-block:: shell

   $ git clone https://git.kernel.org/pub/scm/linux/security/vulns.git ~/vulns

Or if already checked out:

.. code-block:: shell

   $ git -C ~/vulns pull

Finally, run the script by using one of the examples below. The most exact are
the first two examples, using the old cve-summary.json.

-  Example using ``--old-cve-report`` as input:

   .. code-block:: shell

      $ python3 openembedded-core/scripts/contrib/improve_kernel_cve_report.py \
         --spdx tmp/deploy/spdx/3.0.1/qemux86_64/recipes/recipe-linux-yocto.spdx.json \
         --datadir ~/vulns \
         --old-cve-report build/tmp/deploy/images/<machine>/<image-name>-<machine>.rootfs.sbom-cve-check.yocto.json

-  Example using ``--debug-sources`` file instead of SPDX kernel file:

   .. code-block:: shell

      $ python3 openembedded-core/scripts/contrib/improve_kernel_cve_report.py \
         --debug-sources tmp/pkgdata/qemux86_64/debugsources/linux-yocto-debugsources.json.zstd \
         --datadir ~/vulns \
         --old-cve-report build/tmp/deploy/images/<machine>/<image-name>-<machine>.rootfs.sbom-cve-check.yocto.json

-  Example using the ``--kernel-version``:

   .. code-block:: shell

      $ python3 openembedded-core/scripts/contrib/improve_kernel_cve_report.py \
         --spdx tmp/deploy/spdx/3.0.1/qemux86_64/recipes/recipe-linux-yocto.spdx.json \
         --kernel-version 6.12.27 \
         --datadir ~/vulns

Example output for a CVE for which the status was changed to "Ignored" because
the source files associated to the CVE were not compiled:

.. code-block:: json

   {
      "id": "CVE-2025-38384",
      "status": "Ignored",
      "detail": "not-applicable-config",
      "summary": "In the Linux kernel, the following vulnerability has been resolved (...)",
      "description": "Source code not compiled by config. {'drivers/mtd/nand/spi/core.c'}"
   }

Example of output for a CVE not in range:

.. code-block:: json

   {
      "id": "CVE-2025-40017",
      "status": "Patched",
      "detail": "fixed-version",
      "summary": "In the Linux kernel, the following vulnerability has been resolved (...)",
      "description": "only affects 6.15 onwards"
   }

Example of output for a CVE that is vulnerable:

.. code-block:: json

   {
      "id": "CVE-2024-58093",
      "status": "Unpatched",
      "detail": "version-in-range",
      "summary": "In the Linux kernel, the following vulnerability has been resolved (...)",
      "description": "Needs backporting (fixed from 6.15)"
   }

Example of output for a CVE rejected by the Linux CNA:

.. code-block:: json

   {
      "id": "CVE-2025-38380",
      "status": "Ignored",
      "detail": "rejected",
      "summary": "In the Linux kernel, the following vulnerability has been resolved (...)",
      "description": "Rejected by CNA"
   }

