#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.contrib import admin
from .models import BuildEnvironment

class BuildEnvironmentAdmin(admin.ModelAdmin):
    pass

admin.site.register(BuildEnvironment, BuildEnvironmentAdmin)
