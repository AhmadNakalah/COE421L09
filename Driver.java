// Updated by Member B
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Shared UI so all threads print safely with no inconsistencies 
class UI {
    public synchronized void print(String message) {
        System.out.println(message);
    }
}

// Observer interface → anything that wants to react to Eren implements this
interface ErenObserver {
    void onTransform(int energy);
}

// Eren runs on a thread and notifies observers whenever he transforms
class Eren implements Runnable {
    private int energy;
    private final UI ui;
    private final List<ErenObserver> observers;
    private final Random random;
    private final Thread thread;

    public Eren(int energy, UI ui) {
        this.energy = energy;
        this.ui = ui;
        this.observers = new ArrayList<>();
        this.random = new Random();
        this.thread = new Thread(this, "Eren-Thread");
        this.thread.start();
    }

    public synchronized void addObserver(ErenObserver observer) {
        observers.add(observer);
    }

    private synchronized void notifyObservers() {
        for (ErenObserver observer : observers) {
            observer.onTransform(energy);
        }
    }

    public synchronized int getEnergy() {
        return energy;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (energy <= 0) {
                    ui.print("Eren has no energy left.");
                    break;
                }
            }

            try {
                int waitTime = 5 + random.nextInt(6);
                Thread.sleep(waitTime * 1000L);

                synchronized (this) {
                    if (energy <= 0) {
                        ui.print("Eren has no energy left.");
                        break;
                    }

                    ui.print("Eren transforms into a Titan! Energy: " + energy);
                    notifyObservers();
                    energy -= 5;
                }

                Thread.sleep(10000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ui.print("Eren's thread was interrupted.");
                break;
            }
        }
    }
}

// Levi reacts to transformations by losing patience
class TeamLeader implements ErenObserver {
    private final String name;
    private int patience;
    private final UI ui;
    private boolean active;

    public TeamLeader(String name, int patience, UI ui, Eren eren) {
        this.name = name;
        this.patience = patience;
        this.ui = ui;
        this.active = true;
        eren.addObserver(this);
    }

    @Override
    public synchronized void onTransform(int energy) {
        if (!active) return;

        patience -= 5;

        if (patience == 10) {
            ui.print(name + ": My patience is waning!");
        } else if (patience == 5) {
            ui.print(name + ": Is this entertaining?");
        } else if (patience <= 0) {
            ui.print(name + ": Omae wa mou shindeiru");
            active = false;
        }
    }
}

// Friends react randomly and care about Eren’s safety
class Friend implements ErenObserver {
    private final String name;
    private final UI ui;
    private final Random random;

    public Friend(String name, UI ui, Eren eren) {
        this.name = name;
        this.ui = ui;
        this.random = new Random();
        eren.addObserver(this);
    }

    @Override
    public void onTransform(int energy) {
        if (random.nextBoolean()) {
            ui.print(name + ": Eren, please be safe!");
        }

        if (energy < 20) {
            ui.print(name + ": Rage, rage against the dying of the light");
        }
    }
}

// Titans may attack depending on probability
class Titan implements ErenObserver {
    private final String name;
    private final UI ui;
    private final Random random;

    public Titan(String name, UI ui, Eren eren) {
        this.name = name;
        this.ui = ui;
        this.random = new Random();
        eren.addObserver(this);
    }

    @Override
    public void onTransform(int energy) {
        if (random.nextBoolean()) {
            ui.print(name + " attacks Eren!");
        }

        if (energy > 40) {
            ui.print(name + ": Now we got problems, and I don't think we can solve 'em");
        }
    }
}

public class Driver {
    public static void main(String[] args) {
        UI ui = new UI();

        Eren e = new Eren(50, ui);

        TeamLeader Levi = new TeamLeader("Levi Ackerman", 20, ui, e);

        Friend Mikasa = new Friend("Mikasa", ui, e);
        Friend Armin = new Friend("Armin", ui, e);

        Titan Armor = new Titan("Armored Titan", ui, e);
        Titan Colossal = new Titan("Colossal Titan", ui, e);
    }
}

