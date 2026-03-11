#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# BitBake Toaster UI tests implementation
#
# Copyright (C) 2023 Savoir-faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django import forms
from django.core.validators import FileExtensionValidator

class LoadFileForm(forms.Form):
    eventlog_file = forms.FileField(widget=forms.FileInput(attrs={'accept': '.json'}))
