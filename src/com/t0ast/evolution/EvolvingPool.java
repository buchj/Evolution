/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.evolution;

import com.t0ast.evolution.entities.Entity;
import com.t0ast.evolution.entities.EntityGenerator;
import com.t0ast.evolution.entities.Mutator;
import com.t0ast.evolution.misc.selectors.ListElementSelector;
import com.t0ast.evolution.training.trainers.Trainer;
import com.t0ast.evolution.training.TrainingResults;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author T0astBread
 */
public class EvolvingPool<E extends Entity, R extends TrainingResults>
{
    private List<E> entities;
    private Trainer<E> trainer;
    private int entitiesInGeneration, deathsPerGeneration;
    private ListElementSelector killSelector, breedingSelector;
    private Mutator<E> mutator;
    private MutationType mutationType;
    private EntityGenerator<E> generator;
    private Set<Entity> haveBeenReproducingInThisGeneration;

    public EvolvingPool(Trainer<E> trainer, int entitiesInGeneration, int deathsPerGeneration, ListElementSelector killSelector, ListElementSelector breedingSelector, Mutator<E> mutator, MutationType mutationType, EntityGenerator<E> generator)
    {
        this.entities = new ArrayList<>(entitiesInGeneration);
        this.trainer = trainer;
        this.entitiesInGeneration = entitiesInGeneration;
        this.deathsPerGeneration = deathsPerGeneration;
        this.killSelector = killSelector;
        this.breedingSelector = breedingSelector;
        this.mutator = mutator;
        this.mutationType = mutationType;
        this.generator = generator;
        this.haveBeenReproducingInThisGeneration = new HashSet<>();
    }
    
    public EvolvingPool<E, R> initialize()
    {
        for(int i = 0; i < this.entitiesInGeneration; i++)
        {
            this.entities.add(this.generator.generateRandomEntity());
        }
        return this;
    }
    
    public void nextGen()
    {
        this.trainer.trainGeneration(this.entities);
        this.entities.sort(null);
        for(int i = 0; i < this.deathsPerGeneration; i++)
        {
            this.entities.remove(this.killSelector.selectIndex(this.entities));
        }
        
        List<E> newEntitiesTemp = new ArrayList<>();
        for(int i = this.entities.size(); i < this.entitiesInGeneration; i++)
        {
            if(this.mutationType == MutationType.SINGLE_PARENT)
                newEntitiesTemp.add(this.mutator.mutate(selectForBreeding()));
            else if(this.mutationType == MutationType.DOUBLE_PARENT)
                newEntitiesTemp.add(this.mutator.mutate(selectForBreedingDouble(), selectForBreedingDouble())); //selectForBreeding nimmt jeden entity nur einmal dran - PROGLEM!!
        }
        this.entities.addAll(newEntitiesTemp);
        this.haveBeenReproducingInThisGeneration.clear();
    }
    
    private E selectForBreeding()
    {
        E selected = null;
        do
        {
            selected = this.breedingSelector.selectFrom(this.entities);
        }
        while(this.haveBeenReproducingInThisGeneration.contains(selected));
        this.haveBeenReproducingInThisGeneration.add(selected);
        return selected;
    }
    
    private E selectForBreedingDouble()
    {
        return this.breedingSelector.selectFrom(entities);
        
    }

    public List<E> getEntities()
    {
        return entities;
    }
    
    public static enum MutationType
    {
        SINGLE_PARENT, DOUBLE_PARENT
    }
}
