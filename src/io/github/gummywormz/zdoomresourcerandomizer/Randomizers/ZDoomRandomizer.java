/*
 * Copyright (C) 2015 Paul Alves
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.gummywormz.zdoomresourcerandomizer.Randomizers;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface for a generic randomizer
 * @author Paul Alves
 * @param <T> The type to be accepted by the addEntries method
 */
public interface ZDoomRandomizer<T> {
    public void processFile(File f) throws IOException;
    public void addEntries(List<T> entries);
    public void write(File f) throws IOException;
}
