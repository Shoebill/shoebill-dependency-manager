/**
 * Copyright (C) 2012 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill.dependency;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * @author MK124
 * @author Marvin Haschker
 */
public class ShoebillRepositoryListener extends AbstractRepositoryListener
{
	@Override
	public void artifactDescriptorInvalid(RepositoryEvent event)
	{
		System.err.println("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException()
				.getMessage());
	}
	
	@Override
	public void artifactDescriptorMissing(RepositoryEvent event)
	{
		System.err.println("Missing artifact descriptor for " + event.getArtifact());
	}
	
	@Override
	public void artifactResolved(RepositoryEvent event)
	{
		if (event.getArtifact().getExtension().equals("pom")) return;
		System.out.println("Resolved artifact " + event.getArtifact() + " from " + event.getRepository().getId());
	}
	
	@Override
	public void artifactDownloading(RepositoryEvent event)
	{
		if (event.getArtifact().getExtension().equals("pom")) return;
		System.out.println("Downloading artifact " + event.getArtifact() + " from " + event.getRepository().getId());
	}
	
	@Override
	public void artifactDownloaded(RepositoryEvent event)
	{
		if (event.getArtifact().getExtension().equals("pom")) return;
		if (event.getException() != null) return;
		System.out.println("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository().getId());
	}
	
	@Override
	public void metadataInvalid(RepositoryEvent event)
	{
		System.err.println("Invalid metadata " + event.getMetadata());
	}
}
