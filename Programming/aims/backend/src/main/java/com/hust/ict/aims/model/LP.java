package com.hust.ict.aims.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lp")
@DiscriminatorValue("LP")
public class LP extends MusicDisc {
    // Không cần khai báo lại các trường chung
}
