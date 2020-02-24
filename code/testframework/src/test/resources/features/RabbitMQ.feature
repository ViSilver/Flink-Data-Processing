Feature: Testing Spring RabbitMQ integration

  Scenario: Testing sending several messages
    When User sends 50000 message
#    Then A message is received

  @RunOnlyThis
  Scenario: Testing reading messages from csv and sending them to RabbitMQ
    Given The exchanges are created
      | Name   | Type   | Durable |
      | alarms | direct | true    |
    And The queues are created
      | Name                        | Durable |
      | total.battery.voltage.queue | true    |
      | power.grid.voltage.queue    | true    |
      | equipment.failed.queue      | true    |
      | my.output                   | true    |
      | late.events                 | true    |
    And The bindings are set
      | Exchange | Queue                       | Routing key |
      | alarms   | total.battery.voltage.queue | 141         |
      | alarms   | power.grid.voltage.queue    | 121         |
      | alarms   | equipment.failed.queue      | 303         |
    When Events are read from file data_bts_bts-data-alarm-2017.csv and sent to exchange alarms
    Then User waits for 15 minutes for logs

  Scenario: Testing reading messages from csv and sending them to RabbitMQ with errors
    Given The exchanges are created
      | Name   | Type   | Durable |
      | alarms | direct | true    |
    And The queues are created
      | Name                        | Durable |
      | total.battery.voltage.queue | true    |
      | power.grid.voltage.queue    | true    |
      | equipment.failed.queue      | true    |
      | my.output                   | true    |
      | late.events                 | true    |
    And The bindings are set
      | Exchange | Queue                       | Routing key |
      | alarms   | total.battery.voltage.queue | 141         |
      | alarms   | power.grid.voltage.queue    | 121         |
      | alarms   | equipment.failed.queue      | 303         |
    When Events are read from file data_bts_bts-data-alarm-2017.csv and sent with error rate 1 out of 100 to exchange alarms
    Then User waits for 15 minutes for logs
