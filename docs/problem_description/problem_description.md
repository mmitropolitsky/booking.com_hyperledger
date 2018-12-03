# Problem Description

There are three main actors in the business ecosystem we are considering :
* Partners : individuals, hotels, hostels, etc ... they want to earn money by renting out a room / an apartment / 
a house (referred to as **rental assets**) for short periods of time for which they are not using them.
* Online Travel Agencies (**OTAs**) : they offer to manage advertisement and bookings for their partners, offering more
visibility through their platform. They earn a service fee and/or commission. 
* Governments : they want to regulate short-term renting

## Actors

### Partners

In order to rent out a rental asset, the partners need to :
1. define which dates are available
2. accept booking requests
3. cancel booking requests if needed

In order to handle possible cancellation policies in place, they also need to :
* confirm the stay

### OTA

In order to manage a rental asset for a Partner, OTAs need to be able to :
1. accept booking requests on behalf of the Partner
2. cancel booking requests on behalf of the Customer

To offer relevant information to their customers (search for available places), OTAs need to be able to :
* check available dates for their Partners rental assets

Currently availability and booking information is not shared between different OTAs, for competition reasons, and can
lead to overbooking of rental assets which are difficult to handle on the OTA's side and cause a negative customer 
experience.

A publicly available, distributed, ordered and immutable record of bookings could solve this issue and is the reason why
a blockchain-based solution is investigated here.

However the proposed solution should respect the privacy requirements of the business context :
1. All OTAs Partner1 (P1) has authorized should be able to see which dates are available for P1's assets (those for 
which they were authorized)
2. If multiple OTAs request bookings that have overlapping dates, only one booking should be accepted by the system
3. A given OTA should not be able to tell which other OTA is responsible for given dates not being available anymore 
(i.e. booking)
4. A given OTA should not be able to tell which other OTAs are authorized by their Partners for the same assets they are.

### Government

In the scope of this project we reduce the Governments' regulatory efforts to a simple thing : defining a maximum number
of days/nights an individual (i.e. Partner) is allowed to rent an asset for, in a year.
This yearly cap is referred to as **nightcap**.

Blockchain technology enables the use of **Smart contracts** which can, among other things, define the conditions for
transactions to occur on the blockchain. Enforcing the nightcap limitation through smart contracts is therefore possible
and constitutes another reason why such a solution is explored here.

In order to enforce nightcap regulation, the system needs to :
1. apply the remaining number of nights allowed when fulfilling check availability requests
2. reject booking requests that would violate this limit

### Summary

![Use case diagram](https://g.gravizo.com/svg?
    @startuml
    left to right direction
    actor Government
    actor OTA
    actor Partner
    rectangle Rental_Asset {
        rectangle Rent_out as rent {
            usecase (Put available dates) as def_avail
            usecase (Book date) as book
            usecase (Cancel booking) as cancel
            usecase (Confirm stay) as confirm
        }
        usecase (Check available dates) as check
        Partner --> rent
        Partner ..> OTA : \n\nauthorize
        book <-- OTA
        cancel <-- OTA
        check <-- OTA
        Government --> rent : regulate
    }
    @enduml
)