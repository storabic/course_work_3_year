# Generated by Django 3.2.6 on 2021-09-11 14:06

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('coursework', '0005_auto_20210911_1350'),
    ]

    operations = [
        migrations.RenameField(
            model_name='subject',
            old_name='pid',
            new_name='pool',
        ),
        migrations.AddField(
            model_name='pool',
            name='name',
            field=models.CharField(default=1, max_length=40),
            preserve_default=False,
        ),
    ]