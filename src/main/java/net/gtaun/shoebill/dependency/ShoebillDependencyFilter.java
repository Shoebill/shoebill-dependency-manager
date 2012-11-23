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

import java.io.File;
import java.util.List;

import net.gtaun.shoebill.ShoebillArtifactLocator;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;

/**
 * 
 * 
 * @author MK124
 */
public class ShoebillDependencyFilter implements DependencyFilter
{
	private ShoebillArtifactLocator artifactLocator;
	
	
	public ShoebillDependencyFilter(ShoebillArtifactLocator locator)
	{
		this.artifactLocator = locator;
	}
	
	@Override
	public boolean accept(DependencyNode node, List<DependencyNode> parents)
	{
		Dependency dependency = node.getDependency();
		if(dependency != null)
		{
			Artifact artifact = dependency.getArtifact();
			String coord = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getBaseVersion();
			
			File file = artifactLocator.getOverrideFile(coord);
			if (file != null)
			{
				System.out.println("Ignored artifact " + artifact + " (Override: " + file + ")");
				return false;
			}
		}
		
		return true;
	}
}
