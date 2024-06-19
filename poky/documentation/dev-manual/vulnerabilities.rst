.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Checking for Vulnerabilities
****************************

Vulnerabilities in Poky and OE-Core
===================================

The Yocto Project has an infrastructure to track and address unfixed
known security vulnerabilities, as tracked by the public
:wikipedia:`Common Vulnerabilities and Exposures (CVE) <Common_Vulnerabilities_and_Exposures>`
database.

The Yocto Project maintains a `list of known vulnerabilities
<https://autobuilder.yocto.io/pub/non-release/patchmetrics/>`__
for packages in Poky and OE-Core, tracking the evolution of the number of
unpatched CVEs and the status of patches. Such information is available for
the current development version and for each supported release.

Security is a process, not a product, and thus at any time, a number of security
issues may be impacting Poky and OE-Core. It is up to the maintainers, users,
contributors and anyone interested in the issues to investigate and possibly fix them by
updating software components to newer versions or by applying patches to address them.
It is recommended to work with Poky and OE-Core upstream maintainers and submit
patches to fix them, see ":doc:`../contributor-guide/submit-changes`" for details.

Vulnerability check at build time
=================================

To enable a check for CVE security vulnerabilities using
:ref:`ref-classes-cve-check` in the specific image or target you are building,
add the following setting to your configuration::

   INHERIT += "cve-check"

The CVE database contains some old incomplete entries which have been
deemed not to impact Poky or OE-Core. These CVE entries can be excluded from the
check using build configuration::

   include conf/distro/include/cve-extra-exclusions.inc

With this CVE check enabled, BitBake build will try to map each compiled software component
recipe name and version information to the CVE database and generate recipe and
image specific reports. These reports will contain:

-  metadata about the software component like names and versions

-  metadata about the CVE issue such as description and NVD link

-  for each software component, a list of CVEs which are possibly impacting this version

-  status of each CVE: ``Patched``, ``Unpatched`` or ``Ignored``

The status ``Patched`` means that a patch file to address the security issue has been
applied. ``Unpatched`` status means that no patches to address the issue have been
applied and that the issue needs to be investigated. ``Ignored`` means that after
analysis, it has been deemed to ignore the issue as it for example affects
the software component on a different operating system platform.

By default, no NVD API key is used to retrieve data from the CVE database, which
results in larger delays between NVD API requests. See the :term:`NVDCVE_API_KEY`
documentation on how to request and set a NVD API key.

After a build with CVE check enabled, reports for each compiled source recipe will be
found in ``build/tmp/deploy/cve``.

For example the CVE check report for the ``flex-native`` recipe looks like::

   $ cat poky/build/tmp/deploy/cve/flex-native
   LAYER: meta
   PACKAGE NAME: flex-native
   PACKAGE VERSION: 2.6.4
   CVE: CVE-2016-6354
   CVE STATUS: Patched
   CVE SUMMARY: Heap-based buffer overflow in the yy_get_next_buffer function in Flex before 2.6.1 might allow context-dependent attackers to cause a denial of service or possibly execute arbitrary code via vectors involving num_to_read.
   CVSS v2 BASE SCORE: 7.5
   CVSS v3 BASE SCORE: 9.8
   VECTOR: NETWORK
   MORE INFORMATION: https://nvd.nist.gov/vuln/detail/CVE-2016-6354

   LAYER: meta
   PACKAGE NAME: flex-native
   PACKAGE VERSION: 2.6.4
   CVE: CVE-2019-6293
   CVE STATUS: Ignored
   CVE SUMMARY: An issue was discovered in the function mark_beginning_as_normal in nfa.c in flex 2.6.4. There is a stack exhaustion problem caused by the mark_beginning_as_normal function making recursive calls to itself in certain scenarios involving lots of '*' characters. Remote attackers could leverage this vulnerability to cause a denial-of-service.
   CVSS v2 BASE SCORE: 4.3
   CVSS v3 BASE SCORE: 5.5
   VECTOR: NETWORK
   MORE INFORMATION: https://nvd.nist.gov/vuln/detail/CVE-2019-6293

For images, a summary of all recipes included in the image and their CVEs is also
generated in textual and JSON formats. These ``.cve`` and ``.json`` reports can be found
in the ``tmp/deploy/images`` directory for each compiled image.

At build time CVE check will also throw warnings about ``Unpatched`` CVEs::

   WARNING: flex-2.6.4-r0 do_cve_check: Found unpatched CVE (CVE-2019-6293), for more information check /poky/build/tmp/work/core2-64-poky-linux/flex/2.6.4-r0/temp/cve.log
   WARNING: libarchive-3.5.1-r0 do_cve_check: Found unpatched CVE (CVE-2021-36976), for more information check /poky/build/tmp/work/core2-64-poky-linux/libarchive/3.5.1-r0/temp/cve.log

It is also possible to check the CVE status of individual packages as follows::

   bitbake -c cve_check flex libarchive

Fixing CVE product name and version mappings
============================================

By default, :ref:`ref-classes-cve-check` uses the recipe name :term:`BPN` as CVE
product name when querying the CVE database. If this mapping contains false positives, e.g.
some reported CVEs are not for the software component in question, or false negatives like
some CVEs are not found to impact the recipe when they should, then the problems can be
in the recipe name to CVE product mapping. These mapping issues can be fixed by setting
the :term:`CVE_PRODUCT` variable inside the recipe. This defines the name of the software component in the
upstream `NIST CVE database <https://nvd.nist.gov/>`__.

The variable supports using vendor and product names like this::

   CVE_PRODUCT = "flex_project:flex"

In this example the vendor name used in the CVE database is ``flex_project`` and the
product is ``flex``. With this setting the ``flex`` recipe only maps to this specific
product and not products from other vendors with same name ``flex``.

Similarly, when the recipe version :term:`PV` is not compatible with software versions used by
the upstream software component releases and the CVE database, these can be fixed using
the :term:`CVE_VERSION` variable.

Note that if the CVE entries in the NVD database contain bugs or have missing or incomplete
information, it is recommended to fix the information there directly instead of working
around the issues possibly for a long time in Poky and OE-Core side recipes. Feedback to
NVD about CVE entries can be provided through the `NVD contact form <https://nvd.nist.gov/info/contact-form>`__.

Fixing vulnerabilities in recipes
=================================

Suppose a CVE security issue impacts a software component. In that case, it can
be fixed by updating to a newer version, by applying a patch, or by marking it
as patched via :term:`CVE_STATUS` variable flag. For Poky and OE-Core master
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


For the correct operations of the ``cve-check``, it requires the CVE
identification in a ``CVE:`` tag of the patch file commit message using
the format::

   CVE: CVE-2022-3341

It is also recommended to add the ``Upstream-Status:`` tag with a link
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

CVE checker will then capture this information and change the CVE status to ``Patched``
in the generated reports.

If analysis shows that the CVE issue does not impact the recipe due to configuration, platform,
version or other reasons, the CVE can be marked as ``Ignored`` by using
the :term:`CVE_STATUS` variable flag with appropriate reason which is mapped to ``Ignored``.
The entry should have the format like::

   CVE_STATUS[CVE-2016-10642] = "cpe-incorrect: This is specific to the npm package that installs cmake, so isn't relevant to OpenEmbedded"

As mentioned previously, if data in the CVE database is wrong, it is recommended
to fix those issues in the CVE database (NVD in the case of OE-core and Poky)
directly.

Note that if there are many CVEs with the same status and reason, those can be
shared by using the :term:`CVE_STATUS_GROUPS` variable.

Recipes can be completely skipped by CVE check by including the recipe name in
the :term:`CVE_CHECK_SKIP_RECIPE` variable.

Implementation details
======================

Here's what the :ref:`ref-classes-cve-check` class does to find unpatched CVE IDs.

First the code goes through each patch file provided by a recipe. If a valid CVE ID
is found in the name of the file, the corresponding CVE is considered as patched.
Don't forget that if multiple CVE IDs are found in the filename, only the last
one is considered. Then, the code looks for ``CVE: CVE-ID`` lines in the patch
file. The found CVE IDs are also considered as patched.
Additionally ``CVE_STATUS`` variable flags are parsed for reasons mapped to ``Patched``
and these are also considered as patched.

Then, the code looks up all the CVE IDs in the NIST database for all the
products defined in :term:`CVE_PRODUCT`. Then, for each found CVE:

-  If the package name (:term:`PN`) is part of
   :term:`CVE_CHECK_SKIP_RECIPE`, it is considered as ``Patched``.

-  If the CVE ID has status ``CVE_STATUS[<CVE ID>] = "ignored"`` or if it's set to
   any reason which is mapped to status ``Ignored`` via ``CVE_CHECK_STATUSMAP``,
   it is  set as ``Ignored``.

-  If the CVE ID is part of the patched CVE for the recipe, it is
   already considered as ``Patched``.

-  Otherwise, the code checks whether the recipe version (:term:`PV`)
   is within the range of versions impacted by the CVE. If so, the CVE
   is considered as ``Unpatched``.

The CVE database is stored in :term:`DL_DIR` and can be inspected using
``sqlite3`` command as follows::

   sqlite3 downloads/CVE_CHECK/nvdcve_1.1.db .dump | grep CVE-2021-37462

When analyzing CVEs, it is recommended to:

-  study the latest information in `CVE database <https://nvd.nist.gov/vuln/search>`__.

-  check how upstream developers of the software component addressed the issue, e.g.
   what patch was applied, which upstream release contains the fix.

-  check what other Linux distributions like `Debian <https://security-tracker.debian.org/tracker/>`__
   did to analyze and address the issue.

-  follow security notices from other Linux distributions.

-  follow public `open source security mailing lists <https://oss-security.openwall.org/wiki/mailing-lists>`__ for
   discussions and advance notifications of CVE bugs and software releases with fixes.

