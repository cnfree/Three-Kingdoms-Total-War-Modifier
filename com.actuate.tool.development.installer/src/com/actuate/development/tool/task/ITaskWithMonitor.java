
package com.actuate.development.tool.task;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ITaskWithMonitor
{

	void execute( final IProgressMonitor monitor );
}
