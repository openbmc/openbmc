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

To enable a check for CVE security vulnerabilities using
:ref:`ref-classes-cve-check` in the specific image or target you are building,
add the following setting to your configuration::

   INHERIT += "cve-check"

The CVE database contains some old incomplete entries which have been
deemed not to impact :term:`OpenEmbedded-Core (OE-Core)`. These CVE entries can be excluded from the
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

   $ cat ./tmp/deploy/cve/flex-native_cve.json
   {
     "version": "1",
     "package": [
       {
         "name": "flex-native",
         "layer": "meta",
         "version": "2.6.4",
         "products": [
           {
             "product": "flex",
             "cvesInRecord": "No"
           },
           {
             "product": "flex",
             "cvesInRecord": "Yes"
           }
         ],
         "issue": [
           {
             "id": "CVE-2006-0459",
             "status": "Patched",
             "link": "https://nvd.nist.gov/vuln/detail/CVE-2006-0459",
             "summary": "flex.skl in Will Estes and John Millaway Fast Lexical Analyzer Generator (flex) before 2.5.33 does not allocate enough memory for grammars containing (1) REJECT statements or (2) trailing context rules, which causes flex to generate code that contains a buffer overflow that might allow context-dependent attackers to execute arbitrary code.",
             "scorev2": "7.5",
             "scorev3": "0.0",
             "scorev4": "0.0",
             "modified": "2024-11-21T00:06Z",
             "vector": "NETWORK",
             "vectorString": "AV:N/AC:L/Au:N/C:P/I:P/A:P",
             "detail": "version-not-in-range"
           },
           {
             "id": "CVE-2016-6354",
             "status": "Patched",
             "link": "https://nvd.nist.gov/vuln/detail/CVE-2016-6354",
             "summary": "Heap-based buffer overflow in the yy_get_next_buffer function in Flex before 2.6.1 might allow context-dependent attackers to cause a denial of service or possibly execute arbitrary code via vectors involving num_to_read.",
             "scorev2": "7.5",
             "scorev3": "9.8",
             "scorev4": "0.0",
             "modified": "2024-11-21T02:55Z",
             "vector": "NETWORK",
             "vectorString": "AV:N/AC:L/Au:N/C:P/I:P/A:P",
             "detail": "version-not-in-range"
           },
           {
             "id": "CVE-2019-6293",
             "status": "Ignored",
             "link": "https://nvd.nist.gov/vuln/detail/CVE-2019-6293",
             "summary": "An issue was discovered in the function mark_beginning_as_normal in nfa.c in flex 2.6.4. There is a stack exhaustion problem caused by the mark_beginning_as_normal function making recursive calls to itself in certain scenarios involving lots of '*' characters. Remote attackers could leverage this vulnerability to cause a denial-of-service.",
             "scorev2": "4.3",
             "scorev3": "5.5",
             "scorev4": "0.0",
             "modified": "2024-11-21T04:46Z",
             "vector": "NETWORK",
             "vectorString": "AV:N/AC:M/Au:N/C:N/I:N/A:P",
             "detail": "upstream-wontfix",
             "description": "there is stack exhaustion but no bug and it is building the parser, not running it, effectively similar to a compiler ICE. Upstream no plans to address this."
           }
         ]
       }
     ]
   }

For images, a summary of all recipes included in the image and their CVEs is also
generated in the JSON format. These ``.json`` reports can be found
in the ``tmp/deploy/images`` directory for each compiled image.

At build time CVE check will also throw warnings about ``Unpatched`` CVEs::

   WARNING: qemu-native-9.2.0-r0 do_cve_check: Found unpatched CVE (CVE-2023-1386)

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
as patched via :term:`CVE_STATUS` variable flag. For OE-Core master
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
to fix those issues in the CVE database (NVD in the case of
:term:`OpenEmbedded-Core (OE-Core)`) directly.

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

   sqlite3 downloads/CVE_CHECK2/nvd*.db .dump | grep CVE-2021-37462

When analyzing CVEs, it is recommended to:

-  study the latest information in `CVE database <https://nvd.nist.gov/vuln/search>`__.

-  check how upstream developers of the software component addressed the issue, e.g.
   what patch was applied, which upstream release contains the fix.

-  check what other Linux distributions like `Debian <https://security-tracker.debian.org/tracker/>`__
   did to analyze and address the issue.

-  follow security notices from other Linux distributions.

-  follow public `open source security mailing lists <https://oss-security.openwall.org/wiki/mailing-lists>`__ for
   discussions and advance notifications of CVE bugs and software releases with fixes.

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
:ref:`ref-classes-cve-check` or :ref:`ref-classes-vex` report.

``improve_kernel_cve_report.py``
--------------------------------

The ``openembedded-core/scripts/contrib/improve_kernel_cve_report.py`` script
leverages CVE kernel metadata and the :term:`SPDX_INCLUDE_COMPILED_SOURCES`
variable to update a ``cve-summary.json`` file. It reduces CVE false
positives by 70%-80% and provide detailed responses for all kernel-related
CVEs by analyzing the files used to build the kernel. The script is decoupled from
the build and can be run outside of the :term:`BitBake` environment.

The script uses the output from the :ref:`ref-classes-vex` or
:ref:`ref-classes-cve-check` class as input, together with CVE information from
the Linux kernel CNA to enrich the ``cve-summary.json`` file with updated CVE
information.

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
         --old-cve-report build/tmp/log/cve/cve-summary.json

-  Example using ``--debug-sources`` file instead of SPDX kernel file:

   .. code-block:: shell

      $ python3 openembedded-core/scripts/contrib/improve_kernel_cve_report.py \
         --debug-sources tmp/pkgdata/qemux86_64/debugsources/linux-yocto-debugsources.json.zstd \
         --datadir ~/vulns \
         --old-cve-report build/tmp/log/cve/cve-summary.json

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

