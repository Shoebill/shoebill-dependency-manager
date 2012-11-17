package net.gtaun.shoebill.dependency;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.io.PrintStream;

import org.sonatype.aether.AbstractRepositoryListener;
import org.sonatype.aether.RepositoryEvent;

/**
 * A simplistic repository listener that logs events to the console.
 */
public class ShoebillRepositoryListener extends AbstractRepositoryListener
{
	private PrintStream out;
	
	
	public ShoebillRepositoryListener()
	{
		this(null);
	}
	
	public ShoebillRepositoryListener(PrintStream out)
	{
		this.out = (out != null) ? out : System.out;
	}
	
	public void artifactDescriptorInvalid(RepositoryEvent event)
	{
		out.println("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException().getMessage());
	}
	
	public void artifactDescriptorMissing(RepositoryEvent event)
	{
		out.println("Missing artifact descriptor for " + event.getArtifact());
	}
	
	public void artifactResolved(RepositoryEvent event)
	{
		out.println("Resolved artifact " + event.getArtifact() + " from " + event.getRepository().getId());
	}
	
	public void artifactDownloading(RepositoryEvent event)
	{
		if(event.getArtifact().getExtension() == "pom") return;
		out.println("Downloading artifact " + event.getArtifact() + " from " + event.getRepository().getId());
	}
	
	public void artifactDownloaded(RepositoryEvent event)
	{
		if(event.getArtifact().getExtension() == "pom") return;
		out.println("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository().getId());
	}
	
	public void metadataInvalid(RepositoryEvent event)
	{
		out.println("Invalid metadata " + event.getMetadata());
	}
}
