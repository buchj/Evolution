/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.evolution.entities;

/**
 *
 * @author T0astBread
 */
public interface Mutator<E extends Entity>
{
    E mutate(E entity);
    E mutate(E parent1, E parent2);
}
