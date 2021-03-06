/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.ext.search.neigh;

import org.jamesframework.core.subset.neigh.moves.SwapMove;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.core.subset.neigh.moves.DeletionMove;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.*;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test CompositeNeighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CompositeNeighbourhoodTest {
    
    // constants
    private static final int NUM_IDS = 4;
    private static final int MIN_SUBSET_SIZE = 1;
    private static final int MAX_SUBSET_SIZE = 3;
    private static final int SUBSET_SIZE = 2;
    
    // IDs
    private static Set<Integer> IDs;
    
    // neighbourhoods to be included in composite neighbourhood (single swap + single perturbation)
    private static List<Neighbourhood<SubsetSolution>> neighs;
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing CompositeNeighbourhood ...");
        
        /*****************************************************/
        /* NEIGHBOURHOODS: SINGLE SWAP + SINGLE PERTURBATION */
        /*****************************************************/
        
        neighs = new ArrayList<>();
        neighs.add(new SingleSwapNeighbourhood(){
            @Override
            public String toString(){
                return "Single swap (subset)";
            }
        });
        neighs.add(new SinglePerturbationNeighbourhood(MIN_SUBSET_SIZE, MAX_SUBSET_SIZE){
            @Override
            public String toString(){
                return "Single perturbation (subset)";
            }
        });
        
        // init IDs
        IDs = new HashSet<>();
        for(int i=0; i<NUM_IDS; i++){
            IDs.add(i);
        }
        
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing CompositeNeighbourhood!");
    }

    @Test
    public void testConstructor(){
        
        System.out.println(" - test constructor");
        
        List<Double> weights = new ArrayList<>();
        weights.add(1.0);
        weights.add(1.0);
        boolean thrown;
        
        thrown = false;
        try {
            new CompositeNeighbourhood<>(null, weights);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new CompositeNeighbourhood<>(neighs, null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        neighs.add(null);
        weights.add(1.0);
        try {
            new CompositeNeighbourhood<>(neighs, weights);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        neighs.remove(neighs.size()-1);
        weights.remove(weights.size()-1);
        
        thrown = false;
        neighs.add(neighs.get(0));
        weights.add(null);
        try {
            new CompositeNeighbourhood<>(neighs, weights);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        neighs.remove(neighs.size()-1);
        weights.remove(weights.size()-1);
        
        thrown = false;
        neighs.add(neighs.get(0));
        try {
            new CompositeNeighbourhood<>(neighs, weights);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        neighs.remove(neighs.size()-1);
        
        thrown = false;
        try {
            new CompositeNeighbourhood<>(Collections.emptyList(), Collections.emptyList());
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        weights.set(1, 0.0);
        try {
            new CompositeNeighbourhood<>(neighs, weights);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        weights.set(1, -1.0);
        try {
            new CompositeNeighbourhood<>(neighs, weights);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);

    }
    
    /**
     * Test of getRandomMove method, of class CompositeNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // create random subset solution
        SubsetSolution sol = new SubsetSolution(IDs, SetUtilities.getRandomSubset(IDs, SUBSET_SIZE, RG));
        
        // construct list of all moves for both neighbourhoods
        List<? extends Move<? super SubsetSolution>> swapMoves = neighs.get(0).getAllMoves(sol);
        List<? extends Move<? super SubsetSolution>> pertMoves = neighs.get(1).getAllMoves(sol);
        
        // weights (80, 20)
        double swapWeight = 80;
        double pertWeight = 20;
        System.out.println("   # WEIGHTS:");
        System.out.println("   # Single Swap Neighbourhood (swaps only) = " + swapWeight);
        System.out.println("   # Single Perturbation Neighbourhood (swaps, dels, adds) = " + pertWeight);
        Neighbourhood<SubsetSolution> compositeNeigh = new CompositeNeighbourhood<>(neighs, Arrays.asList(swapWeight, pertWeight));
        int numSwap = 0, numDel = 0, numAdd = 0;
        for(int i=0; i<1000; i++){
            // get random move
            Move<? super SubsetSolution> move = compositeNeigh.getRandomMove(sol);
            assertTrue(swapMoves.contains(move) || pertMoves.contains(move));
            // count
            if(move instanceof SwapMove){
                numSwap++;
            }
            if(move instanceof DeletionMove){
                numDel++;
            }
            if(move instanceof AdditionMove){
                numAdd++;
            }
        }
        System.out.println("    >>> Swap moves: " + numSwap);
        System.out.println("    >>> Deletion moves: " + numDel);
        System.out.println("    >>> Addition moves: " + numAdd);
        
        // weights (1, 99)
        swapWeight = 1;
        pertWeight = 99;
        System.out.println("   # WEIGHTS:");
        System.out.println("   # Single Swap Neighbourhood (swaps only) = " + swapWeight);
        System.out.println("   # Single Perturbation Neighbourhood (swaps, dels, adds) = " + pertWeight);
        compositeNeigh = new CompositeNeighbourhood<>(neighs, Arrays.asList(swapWeight, pertWeight));
        numSwap = numDel = numAdd = 0;
        for(int i=0; i<1000; i++){
            // get random move
            Move<? super SubsetSolution> move = compositeNeigh.getRandomMove(sol);
            assertTrue(swapMoves.contains(move) || pertMoves.contains(move));
            // count
            if(move instanceof SwapMove){
                numSwap++;
            }
            if(move instanceof DeletionMove){
                numDel++;
            }
            if(move instanceof AdditionMove){
                numAdd++;
            }
        }
        System.out.println("    >>> Swap moves: " + numSwap);
        System.out.println("    >>> Deletion moves: " + numDel);
        System.out.println("    >>> Addition moves: " + numAdd);
        
        // test case 2: none of the neighbourhoods return any moves
        Neighbourhood<SubsetSolution> emptyNeigh = new Neighbourhood<SubsetSolution>() {
            @Override
            public Move<? super SubsetSolution> getRandomMove(SubsetSolution solution, Random rnd) {
                return null;
            }
            @Override
            public List<? extends Move<? super SubsetSolution>> getAllMoves(SubsetSolution solution) {
                return Collections.emptyList();
            }
        };
        List<Neighbourhood<SubsetSolution>> newNeighs = Arrays.asList(emptyNeigh, emptyNeigh);
        compositeNeigh = new CompositeNeighbourhood<>(newNeighs, Arrays.asList(10.0, 40.0));
        
        assertNull(compositeNeigh.getRandomMove(sol));
        
    }
    
    /**
     * Test of getAllMoves method, of class CompositeNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
    
        System.out.println(" - test getAllMoves");
        
        // create random subset solution
        SubsetSolution sol = new SubsetSolution(IDs, SetUtilities.getRandomSubset(IDs, SUBSET_SIZE, RG));
        
        // construct set of all moves for both neighbourhoods
        List<? extends Move<? super SubsetSolution>> swapMoves = neighs.get(0).getAllMoves(sol);
        List<? extends Move<? super SubsetSolution>> pertMoves = neighs.get(1).getAllMoves(sol);
        
        for(int i=0; i<100; i++){
            // composite neighbourhood with arbitrary weights
            Neighbourhood<SubsetSolution> compositeNeigh = new CompositeNeighbourhood<>(
                                                                neighs,
                                                                Arrays.asList(RG.nextDouble(), RG.nextDouble())
                                                           );
            // get all moves
            List<? extends Move<? super SubsetSolution>> moves = compositeNeigh.getAllMoves(sol);
            // verify
            assertTrue(moves.size() <= swapMoves.size()+pertMoves.size());
            assertTrue(moves.containsAll(swapMoves));
            assertTrue(moves.containsAll(pertMoves));
        }
        
    }
    
    @Test
    public void testToString(){
        
        System.out.println(" - test toString");
        
        // weights (80, 20)
        double swapWeight = 80.0;
        double pertWeight = 20.0;
        Neighbourhood<SubsetSolution> compositeNeigh = new CompositeNeighbourhood<>(
                                                                neighs,
                                                                Arrays.asList(swapWeight, pertWeight)
        );
        
        assertEquals("Composite: (Single swap (subset), 80.0), (Single perturbation (subset), 20.0)",
                     compositeNeigh.toString());
        
    }

}