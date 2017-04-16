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

package net.gtaun.shoebill.dependency.manual;

import org.apache.maven.repository.internal.*;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;

/**
 * 
 * 
 * @author MK124
 */
public class ManualRepositorySystemFactory
{
	public static RepositorySystem newRepositorySystem()
	{
		DefaultServiceLocator locator = new DefaultServiceLocator();
		//locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
		locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
		
		locator.addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
		locator.addService(VersionResolver.class, DefaultVersionResolver.class);
		locator.addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
		locator.addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
		locator.addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);
		
		locator.setServices(WagonProvider.class, new ManualWagonProvider());
		
		return locator.getService(RepositorySystem.class);
	}
}
