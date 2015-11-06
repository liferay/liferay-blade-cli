package com.liferay.blade.upgrade.liferay70;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class JavaClassVisitor extends ASTVisitor
{

	List<MethodDeclaration> methods = new ArrayList<>();
	List<FieldDeclaration> fields = new ArrayList<>();
	List<ImportDeclaration> imports = new ArrayList<>();

	@Override
	public boolean visit( MethodDeclaration node )
	{
		methods.add( node );

		return super.visit( node );
	}

	public List<MethodDeclaration> getMethods()
	{
		return methods;
	}

	@Override
	public boolean visit( FieldDeclaration node )
	{
		fields.add( node );

		return super.visit( node );
	}

	public List<FieldDeclaration> getFields()
	{
		return fields;
	}

	@Override
	public boolean visit( ImportDeclaration node )
	{
		imports.add( node );

		return super.visit( node );
	}

	public List<ImportDeclaration> getImports()
	{
		return imports;
	}
}