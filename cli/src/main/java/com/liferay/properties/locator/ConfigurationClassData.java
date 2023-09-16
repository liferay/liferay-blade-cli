/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.properties.locator;

import com.liferay.blade.cli.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Alberto Chaparro
 */
public class ConfigurationClassData {

	public ConfigurationClassData(InputStream is) throws IOException {
		ClassReader cr = new ClassReader(is);

		cr.accept(new ConfigClassVisitor(), ClassReader.SKIP_CODE);
	}

	public String[] getConfigFields() {
		return _configFields;
	}

	public String getSuperClass() {
		return _superClass;
	}

	private void _addConfigField(String configField) {
		_configFields = ArrayUtil.append(_configFields, configField);
	}

	private void _setSuperClass(String superClass) {
		_superClass = superClass;
	}

	private String[] _configFields = new String[0];
	private String _superClass;

	private class ConfigClassVisitor extends ClassVisitor {

		public ConfigClassVisitor() {
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(
			int version, int access, String name, String signature, String superName, String[] interfaces) {

			if (superName.equals("java/lang/Object") && (interfaces.length == 1)) {

				// When it's an interface and extends from another interface

				_setSuperClass(interfaces[0]);
			}
			else {
				_setSuperClass(superName);
			}

			super.visit(version, access, name, signature, superName, interfaces);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			return new MethodAnnotationScanner(name);
		}

	}

	private class MethodAnnotationScanner extends MethodVisitor {

		public MethodAnnotationScanner(String fieldName) {
			super(Opcodes.ASM5);

			_fieldName = fieldName;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (desc.equals("LaQute/bnd/annotation/metatype/Meta$AD;")) {
				_addConfigField(_fieldName);
			}

			return null;
		}

		private String _fieldName;

	}

}