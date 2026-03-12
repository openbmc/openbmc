.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

************************
Yocto Project Compatible
************************

============
Introduction
============

After the introduction of layers to OpenEmbedded, it quickly became clear
that while some layers were popular and worked well, others developed a
reputation for being "problematic". Those were layers which didn't
interoperate well with others and tended to assume they controlled all
the aspects of the final output.  This usually isn't intentional but happens
because such layers are often created by developers with a particular focus
(e.g. a company's :term:`BSP<Board Support Package (BSP)>`) whilst the end
users have a different one (e.g. integrating that
:term:`BSP<Board Support Package (BSP)>` into a product).

As a result of noticing such patterns and friction between layers, the project
developed the "Yocto Project Compatible" badge program, allowing layers
following the best known practises to be marked as being widely compatible
with other ones. This takes the form of a set of "yes/no" binary answer
questions where layers can declare if they meet the appropriate criteria.
In the second version of the program, a script was added to make validation
easier and clearer, the script is called  ``yocto-check-layer`` and is
available in :term:`OpenEmbedded-Core (OE-Core)`.

See :ref:`dev-manual/layers:making sure your layer is compatible with yocto project`
for details.

========
Benefits
========

:ref:`overview-manual/yp-intro:the yocto project layer model` is powerful
and flexible: it gives users the ultimate power to change pretty much any
aspect of the system but as with most things, power comes with responsibility.
The Yocto Project would like to see people able to mix and match BSPs with
distro configs or software stacks and be able to merge successfully.
Over time, the project identified characteristics in layers that allow them
to operate well together. "anti-patterns" were also found, preventing layers
from working well together.

The intent of the compatibility program is simple: if the layer passes the
compatibility tests, it is considered "well behaved" and should operate
and cooperate well with other compatible layers.

The benefits of compatibility can be seen from multiple different user and
member perspectives. From a hardware perspective
(a :ref:`overview-manual/concepts:bsp layer`), compatibility means the
hardware can be used in many different products and use cases without
impacting the software stacks being run with it. For a company developing
a product, compatibility gives you a specification / standard you can
require in a contract and then know it will have certain desired
characteristics for interoperability. It also puts constraints on how invasive
the code bases are into the rest of the system, meaning that multiple
different separate hardware support layers can coexist (e.g. for multiple
product lines from different hardware manufacturers). This can also make it
easier for one or more parties to upgrade those system components for security
purposes during the lifecycle of a product.

==================
Validating a layer
==================

The badges are available to members of the Yocto Project (as member benefit)
and to open source projects run on a non-commercial basis. However, anyone can
answer the questions and run the script.

The project encourages all layer maintainers to review the questions and the
output from the script against their layer, as the way some layers are
constructed often has unintended consequences. The questions and the script
are designed to highlight known issues which are often easy to solve. This
makes layers easier to use and therefore more popular.

It is intended that over time, the tests will evolve as new best known
practices are identified, and as new interoperability issues are found,
unnecessarily restricting layer interoperability. If anyone becomes aware of
either type, please let the project know through the
:yocto_home:`technical calls </public-virtual-meetings/>`,
the :yocto_home:`mailing lists </community/mailing-lists/>`
or through the :oe_wiki:`Technical Steering Committee (TSC) </TSC>`.
The TSC is responsible for the technical criteria used by the program.

Layers are divided into three types:

-  :ref:`"BSP" or "hardware support"<overview-manual/concepts:bsp layer>`
   layers contain support for particular pieces of hardware. This includes
   kernel and boot loader configuration, and any recipes for firmware or
   kernel modules needed for the hardware. Such layers usually correspond
   to a :term:`MACHINE` setting.

-  :ref:`"distro" layers<overview-manual/concepts:distro layer>` defined
   as layers providing configuration options and settings such as the
   choice of init system, compiler and optimisation options, and
   configuration and choices of software components. This would usually
   correspond to a :term:`DISTRO` setting.

-  "software" layers are usually recipes. A layer might target a
   particular graphical UI or software stack component.

Here are key best practices the program tries to encourage:

-  A layer should clearly show who maintains it, and who change
   submissions and bug reports should be sent to.

-  Where multiple types of functionality are present, the layer should
   be internally divided into sublayers to separate these components.
   That's because some users may only need one of them and separability
   is a key best practice.

-  Adding a layer to a build should not modify that build, unless the
   user changes a configuration setting to activate the layer, by selecting
   a :term:`MACHINE`, a :term:`DISTRO` or a :term:`DISTRO_FEATURES` setting.

-  Layers should be documenting where they don’t support normal "core"
   functionality such as where debug symbols are disabled or missing, where
   development headers and on-target library usage may not work or where
   functionality like the SDK/eSDK would not be expected to work.

The project does test the compatibility status of the core project layers on
its :doc:`Autobuilder </test-manual/understand-autobuilder>`.

The official form to submit compatibility requests with is at
:yocto_home:`/ecosystem/branding/compatible-registration/`.
Applicants can display the badge they get when their application is successful.

