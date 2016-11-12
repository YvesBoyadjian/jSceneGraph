/*
 *
 *  Copyright (C) 2000 Silicon Graphics, Inc.  All Rights Reserved.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  Further, this software is distributed without any warranty that it is
 *  free of the rightful claim of any third person regarding infringement
 *  or the like.  Any license provided herein, whether implied or
 *  otherwise, applies only to this software file.  Patent licenses, if
 *  any, provided herein do not apply to combinations of this program with
 *  other software, or any other product whatsoever.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact information: Silicon Graphics, Inc., 1600 Amphitheatre Pkwy,
 *  Mountain View, CA  94043, or:
 *
 *  http://www.sgi.com
 *
 *  For further information regarding this notice, see:
 *
 *  http://oss.sgi.com/projects/GenInfo/NoticeExplan/
 *
 */


/*
 * Copyright (C) 1990,91   Silicon Graphics, Inc.
 *
 _______________________________________________________________________
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 |
 |   $Revision: 1.3 $
 |
 |   Description:
 |      This file defines the SbTime class for manipulating times
 |
 |   Classes:
 |      SbTime
 |
 |   Author(s)          : Nick Thompson
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor;

import java.time.Instant;

import jscenegraph.port.Mutable;
import net.sourceforge.jpcap.util.Timeval;


////////////////////////////////////////////////////////////////////////////////
//! Class for representation of a time.
/*!
\class SbTime
\ingroup Basics
This class represents and performs operations on time. Operations may be
done in seconds, seconds and microseconds, or using a
<tt>struct timeval</tt>
(defined in <em>/usr/include/sys/time.h</em>).

\par See Also
\par
cftime
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SbTime implements Mutable {
	
	private Timeval t;
	
	// Default constructor. 
	public SbTime() {
		t = new Timeval(0,0);
	}
	
	// Constructor taking seconds. 
	public SbTime(double sec) {
	     if (sec >= 0) {
	    	            long tv_sec = (long)(sec);
	    	            int tv_usec = (int) (0.5 + (sec - tv_sec) * 1000000.0);
	    	            t = new Timeval(tv_sec,tv_usec);
	    	        }
	    	        else
	    	            this.copyFrom((new SbTime(-sec)).operator_minus());
	    	    		
	}
	
	// Constructor taking seconds + microseconds. 
	public SbTime(long sec, int usec) {
		t = new Timeval(sec,usec);
	}

	// Get a zero time. 
	public static SbTime zero() {
		return new SbTime(0,0);
	}
	
	// Get the current time (seconds since Jan 1, 1970). 
	public static SbTime getTimeOfDay() {
		Instant now = Instant.now();
		long sec = now.getEpochSecond();
		int nanos = now.getNano();
		int usec =  nanos / 1000;
		return new SbTime(sec,usec);
	}
	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Sets to the current time.
//
// Use: public

public void
setToTimeOfDay()
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef WIN32
    this.copyFrom(SbTime.getTimeOfDay());
//#else
//    if (-1 == gettimeofday(&t, NULL))
//        perror("gettimeofday");
//#endif
}

	
	
    //! Get time in seconds as a double
	public double              getValue()
          { return (double) t.getSeconds() + (double) t.getMicroSeconds() / 1000000.0; }
  
    //! Get time in milliseconds (for Xt).
    public long       getMsecValue()                     // System long
        { return t.getSeconds() * 1000 + t.getMicroSeconds() / 1000; }

 	
    //! Equality operators.
    public boolean operator_not_equal(final SbTime tm)
        { return ! (this.operator_equal(tm)); }

	// comparison operator
	public boolean operator_equal(SbTime other) {
		return t.compareTo(other.t) == 0;
	}
	
	// add operator (java port)
	public SbTime operator_add(SbTime t1) {
		long tm_sec = t.getSeconds() + t1.t.getSeconds();
		long tm_usec = (long)t.getMicroSeconds() + t1.t.getMicroSeconds();
		 
	    if (tm_usec >= 1000000) {
				   tm_sec += 1;
				   tm_usec -= 1000000;
		}
				  
		 SbTime tm = new SbTime(tm_sec, (int)tm_usec);
		 
		 return tm;
	}
	
	public boolean operator_less_or_equals(SbTime tm) {
		 int comparison = t.compareTo(tm.t); // java port
		 return comparison <= 0;
	}
	
	// Unary negation. 
	public SbTime operator_minus() {
		 return (t.getMicroSeconds() == 0) ? new SbTime(- t.getSeconds(), 0)
				   : new SbTime(- t.getSeconds() - 1, 1000000 - t.getMicroSeconds()); 
	}

////////////////////////////////////////////////////////////////////////
//
// Description:
//
// Use: public

public SbTime operator_minus(final SbTime t1)
//
////////////////////////////////////////////////////////////////////////
{
	final SbTime t0 = this;
	
    long sec; 
    long    usec;                                       // System long

    sec =  t0.t.getSeconds() - t1.t.getSeconds();
    usec = t0.t.getMicroSeconds() - t1.t.getMicroSeconds();

    while (usec < 0 && sec > 0) {
        usec += 1000000;
        sec -= 1;
    }

    return new SbTime(sec, (int)usec);
}

    //! division by another time
   public double                      operator_div(final SbTime tm)
        { return getValue() / tm.getValue(); }

////////////////////////////////////////////////////////////////////////
//
// Description:
//
// Use: public

public SbTime
operator_mul(double s)
//
////////////////////////////////////////////////////////////////////////
{
	final SbTime tm = this;
	
    return new SbTime(tm.getValue() * s);
}


	
	@Override
	public void copyFrom(Object other) {
		SbTime otherTime = (SbTime)other;
		t = otherTime.t;
	}
	
    //! Set time from milliseconds.
    public void                setMsecValue(long msec)        // System long
        { 
    	t = new Timeval( msec/1000, (int)(1000 * (msec % 1000))); 
        }
	
}
